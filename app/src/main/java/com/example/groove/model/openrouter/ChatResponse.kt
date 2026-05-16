package com.example.groove.model.openrouter

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage?,
) {
    data class Choice(
        val message: Message,
        @SerializedName("finish_reason") val finishReason: String?,
    )

    data class Usage(
        @SerializedName("prompt_tokens") val promptTokens: Int,
        @SerializedName("completion_tokens") val completionTokens: Int,
        @SerializedName("total_tokens") val totalTokens: Int,
    )
}
