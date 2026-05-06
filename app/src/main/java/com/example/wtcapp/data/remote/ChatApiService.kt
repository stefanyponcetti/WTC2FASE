package com.example.wtcapp.data.remote

import com.example.wtcapp.data.models.ChatMessage
import com.example.wtcapp.data.models.ChatModel
import com.example.wtcapp.data.models.CreateGroupRequest
import com.example.wtcapp.data.models.UserModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatApiService {
    @GET("api/chat")
    suspend fun getMyChats(
        @Header("Authorization") token: String
    ): List<ChatModel>

    @GET("api/user")
    suspend fun getUsers(
        @Header("Authorization") token: String
    ): List<UserModel>

    @GET("api/chat/{chatId}/messages")
    suspend fun getHistory(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String,
        @Query("page") page: Int = 1
    ): List<ChatMessage>

    @GET("api/chat/{chatId}/members")
    suspend fun getChatMembers(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: String
    ): List<String>

    @POST("api/chat/private/{targetUserId}")
    suspend fun createPrivateChat(
        @Header("Authorization") token: String,
        @Path("targetUserId") targetUserId: String
    ): ChatModel

    @POST("api/chat/group")
    suspend fun createGroupChat(
        @Header("Authorization") token: String,
        @Body body: CreateGroupRequest
    ): ChatModel
}

