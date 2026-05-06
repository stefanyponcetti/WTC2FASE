package com.example.wtcapp.redefinicaoSenha

import ResetPasswordRequest
import com.android.volley.Response
import com.example.wtcapp.login.ForgotPasswordRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

    // No seu arquivo de definições da API (ex: com.example.wtcapp.network.ApiService)
    interface RedefinirSenhaApi {
        // ... outros endpoints

        @POST("/api/User/forgot-password")
        suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Void>

        @POST("/api/User/reset-password")
        suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Void>
    }


