package com.example.wtcapp.login

import java.time.LocalDate
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
    val data_Nasc: String
)

data class ForgotPasswordRequest(
    val email: String
)

data class RegisterResponse(
    val tipoCliente : String,
    val name : String,
    val email: String,
    val senha : String,
    val data_Nasc : Date
)
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String,
)