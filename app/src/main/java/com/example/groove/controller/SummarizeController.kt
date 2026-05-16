package com.example.groove.controller

import android.util.Log
import com.example.groove.model.openrouter.ChatRequest
import com.example.groove.model.openrouter.Message
import com.example.groove.network.OpenRouterClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject

class SummarizeController {
    private val api = OpenRouterClient.api

    suspend fun summarize(text: String): Result<String> = runCatching {
        val response = api.chatCompletion(
            ChatRequest(
                model = OpenRouterClient.DEFAULT_MODEL,
                messages = listOf(
                    Message(
                        role = "system",
                        content = "You are a concise summarizer. Summarize the given text clearly and briefly.",
                    ),
                    Message(role = "user", content = text),
                ),
            )
        )
        response.choices.firstOrNull()?.message?.content
            ?: error("Empty response from model")
    }

    fun summarizeStreaming(text: String, maxWords: Int, model: String): Flow<String> = flow {
        val request = ChatRequest(
            model = model,
            messages = listOf(
                Message(
                    role = "system",
                    content = "Summarize the following text in approximately $maxWords words. Be clear and concise.",
                ),
                Message(role = "user", content = text),
            ),
            maxTokens = (maxWords * 2).coerceAtLeast(256),
            stream = true,
        )
        Log.d("GrooveSSE", "→ POST model=${request.model} maxTokens=${request.maxTokens} contentLen=${text.length}")

        val response = api.streamChatCompletion(request)
        Log.d("GrooveSSE", "← HTTP ${response.code()} ${response.message()}")
        Log.d("GrooveSSE", "   headers=${response.headers()}")

        if (!response.isSuccessful) {
            val errBody = response.errorBody()?.string()
            Log.e("GrooveSSE", "   ERROR body=$errBody")
            if (response.code() == 429) {
                error("Experimental models are busy. Please try with another model.")
            }
            error("HTTP ${response.code()}: $errBody")
        }

        val body = response.body()
        if (body == null) {
            Log.e("GrooveSSE", "   body=null — nothing to stream")
            error("Empty streaming response body")
        }

        val source = body.source()
        var lineCount = 0
        var dataLineCount = 0
        var emitCount = 0

        while (!source.exhausted()) {
            val line = source.readUtf8Line() ?: break
            lineCount++
            Log.d("GrooveSSE", "   raw[$lineCount]: $line")

            if (!line.startsWith("data: ")) continue
            dataLineCount++
            val data = line.removePrefix("data: ").trim()

            if (data == "[DONE]") {
                Log.d("GrooveSSE", "   [DONE] received — dataLines=$dataLineCount emits=$emitCount")
                break
            }

            val content = runCatching {
                JSONObject(data)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("delta")
                    .optString("content", "")
            }.onFailure { Log.e("GrooveSSE", "   JSON parse error on: $data | err=${it.message}") }
                .getOrNull() ?: continue

            if (content.isNotEmpty()) {
                emitCount++
                emit(content)
            }
        }
        Log.d("GrooveSSE", "   stream done — totalLines=$lineCount dataLines=$dataLineCount emits=$emitCount")
    }
}
