package com.kreggscode.bmi.data.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PollinationsApi {
    @POST("openai")
    suspend fun generateText(
        @Body request: RequestBody
    ): Response<PollinationsTextResponse>

    @POST("openai")
    suspend fun analyzeImage(
        @Body request: RequestBody
    ): Response<PollinationsTextResponse>
}

data class PollinationsTextResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class Message(
    val content: String,
    val role: String
)

