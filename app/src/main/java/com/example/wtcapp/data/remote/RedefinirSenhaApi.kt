package com.example.wtcapp.data.remote

import com.example.wtcapp.data.models.ResetPasswordRequest
import com.example.wtcapp.data.models.ForgotPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

    // No seu arquivo de definições da API (ex: com.example.wtcapp.network.ApiService)
    interface RedefinirSenhaApi {
        // ... outros endpoints

        @POST("/api/User/forgot-password")
        suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Void>

        @POST("/api/User/reset-password")
        suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Void>
    }


