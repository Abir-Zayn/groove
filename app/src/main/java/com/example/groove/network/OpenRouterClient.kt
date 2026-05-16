package com.example.groove.network

import com.example.groove.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenRouterClient {
    private const val BASE_URL = "https://openrouter.ai/api/v1/"
    const val DEFAULT_MODEL = "minimax/minimax-m2.5:free"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPENROUTER_API_KEY}")
                    .addHeader("HTTP-Referer", "com.example.groove")
                    .addHeader("X-Title", "Groove")
                    .build()
            )
        }
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        )
        .build()

    val api: OpenRouterApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenRouterApi::class.java)
}
