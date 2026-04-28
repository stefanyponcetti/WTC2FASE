package com.example.wtcapp.services

import com.example.wtcapp.data.ChatMessage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApiService {
    @GET("api/chats/{chatId}/messages")
    suspend fun getHistory(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Query("page") page: Int = 1
    ): List<ChatMessage>
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:5255/"

    val api: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java)
    }
}