package com.example.groove.network

import com.example.groove.model.openrouter.ChatRequest
import com.example.groove.model.openrouter.ChatResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenRouterApi {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatRequest): ChatResponse

    @Streaming
    @POST("chat/completions")
    suspend fun streamChatCompletion(@Body request: ChatRequest): Response<ResponseBody>
}
