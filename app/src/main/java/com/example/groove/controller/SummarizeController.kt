package com.example.groove.controller

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

    fun summarizeStreaming(text: String, maxWords: Int): Flow<String> = flow {
        val response = api.streamChatCompletion(
            ChatRequest(
                model = OpenRouterClient.DEFAULT_MODEL,
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
        )
        val body = response.body() ?: error("Empty streaming response")
        val source = body.source()
        while (!source.exhausted()) {
            val line = source.readUtf8Line() ?: break
            if (!line.startsWith("data: ")) continue
            val data = line.removePrefix("data: ").trim()
            if (data == "[DONE]") break
            val content = runCatching {
                JSONObject(data)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("delta")
                    .optString("content", "")
            }.getOrNull() ?: continue
            if (content.isNotEmpty()) emit(content)
        }
    }
}
