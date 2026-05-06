package com.example.wtcapp.services

import com.example.wtcapp.data.UpdateUsuarioDto
import com.example.wtcapp.data.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface PerfilApiService {


    @GET("usuario/{id}")
    suspend fun buscarUsuario(
        @Path("id") id: String
    ): UsuarioDto


    @PUT("usuario/{id}")
    suspend fun atualizarUsuario(
        @Path("id") id: String,
        @Body usuario: UpdateUsuarioDto
    ): UsuarioDto
}