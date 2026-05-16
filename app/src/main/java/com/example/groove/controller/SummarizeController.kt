package com.example.groove.controller

import com.example.groove.model.openrouter.ChatRequest
import com.example.groove.model.openrouter.Message
import com.example.groove.network.OpenRouterClient

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
}
