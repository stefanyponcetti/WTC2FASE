package com.example.wtcapp.services

import com.example.wtcapp.login.LoginGoogleRequest
import com.example.wtcapp.login.LoginRequest
import com.example.wtcapp.login.LoginResponse
import com.example.wtcapp.login.RegisterRequest
import com.example.wtcapp.login.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("api/User/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/GoogleLogin/login")
    suspend fun loginGoogle(@Body request: LoginGoogleRequest): Response<LoginResponse>

    @POST("api/User/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}