package com.example.groove.network

import com.example.groove.model.openrouter.ChatRequest
import com.example.groove.model.openrouter.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenRouterApi {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse
}
