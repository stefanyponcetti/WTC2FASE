package com.example.wtcapp.data.remote

import com.example.wtcapp.data.models.ChatMessage
import com.example.wtcapp.data.models.ChatModel
import com.example.wtcapp.data.models.ComunicadoModel
import com.example.wtcapp.data.models.ContactNoteResponse
import com.example.wtcapp.data.models.CreateComunicadoRequest
import com.example.wtcapp.data.models.CreateGroupRequest
import com.example.wtcapp.data.models.UpsertNoteRequest
import com.example.wtcapp.data.models.UserModel
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("api/user/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): UserModel

    @GET("api/user/cargos")
    suspend fun getCargos(
        @Header("Authorization") token: String
    ): List<String>

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

    @GET("api/comunicado")
    suspend fun getComunicados(
        @Header("Authorization") token: String
    ): List<ComunicadoModel>

    @POST("api/comunicado")
    suspend fun createComunicado(
        @Header("Authorization") token: String,
        @Body body: CreateComunicadoRequest
    ): ComunicadoModel

    @PUT("api/comunicado/{id}")
    suspend fun updateComunicado(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body body: CreateComunicadoRequest
    ): ComunicadoModel

    @DELETE("api/comunicado/{id}")
    suspend fun deleteComunicado(
        @Header("Authorization") token: String,
        @Path("id") id: String
    )

    @GET("api/contact-note/{contactId}")
    suspend fun getContactNote(
        @Header("Authorization") token: String,
        @Path("contactId") contactId: String
    ): ContactNoteResponse

    @PUT("api/contact-note/{contactId}")
    suspend fun upsertContactNote(
        @Header("Authorization") token: String,
        @Path("contactId") contactId: String,
        @Body body: UpsertNoteRequest
    ): ContactNoteResponse

    @DELETE("api/contact-note/{contactId}")
    suspend fun deleteContactNote(
        @Header("Authorization") token: String,
        @Path("contactId") contactId: String
    )
}

