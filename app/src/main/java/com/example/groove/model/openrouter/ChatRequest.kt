package com.example.groove.model.openrouter

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    @SerializedName("max_tokens") val maxTokens: Int = 1024,
    val stream: Boolean = false,
)
