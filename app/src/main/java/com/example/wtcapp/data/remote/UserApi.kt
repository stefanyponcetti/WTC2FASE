package com.example.wtcapp.data.remote

import com.example.wtcapp.data.models.LoginGoogleRequest
import com.example.wtcapp.data.models.LoginRequest
import com.example.wtcapp.data.models.LoginResponse
import com.example.wtcapp.data.models.RegisterRequest
import com.example.wtcapp.data.models.RegisterResponse
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
