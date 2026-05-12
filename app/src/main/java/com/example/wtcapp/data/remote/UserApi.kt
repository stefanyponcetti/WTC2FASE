package com.example.wtcapp.data.remote

import com.example.wtcapp.data.models.LoginGoogleRequest
import com.example.wtcapp.data.models.LoginRequest
import com.example.wtcapp.data.models.LoginResponse
import com.example.wtcapp.data.models.RegisterRequest
import com.example.wtcapp.data.models.RegisterResponse
import com.example.wtcapp.data.models.UpdateUsuarioDto
import com.example.wtcapp.data.models.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @POST("api/User/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/GoogleLogin/login")
    suspend fun loginGoogle(@Body request: LoginGoogleRequest): Response<LoginResponse>

    @POST("api/User/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("/api/user/{id}")
    suspend fun buscarUsuario(@Header("Authorization") token: String, @Path("id") id: String): UsuarioDto


    @PUT("/api/user/{id}")
    suspend fun atualizarUsuario(@Header("Authorization") token: String, @Path("id") id: String, @Body usuario: UpdateUsuarioDto): UsuarioDto
}
