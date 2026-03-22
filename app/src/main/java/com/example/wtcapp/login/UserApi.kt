package com.example.wtcapp.login

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

interface UserApi {
    @POST("api/User/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/GoogleLogin/login")
    suspend fun loginGoogle(@Body request: LoginGoogleRequest): Response<LoginResponse>
}

