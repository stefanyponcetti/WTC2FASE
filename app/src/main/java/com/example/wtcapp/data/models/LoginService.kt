package com.example.wtcapp.data.models

import java.util.Date

data class LoginRequest(
    val email: String,
    val senha: String
)

data class LoginGoogleRequest(
    val idToken: String
)

data class RegisterRequest(
    val tipoCliente: String,
    val name: String,
    val email: String,
    val senha: String,
    val dataNascimento: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class RegisterResponse(
    val tipoCliente : String,
    val name : String,
    val email: String,
    val senha : String,
    val dataNascimento : Date
)
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val userId: String? = null,
    val id: String? = null,
    val idUsuario: String? = null
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String,
)
