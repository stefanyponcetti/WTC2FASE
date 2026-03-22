package com.example.wtcapp.login

data class LoginRequest(
    val email: String,
    val senha: String
)

data class LoginGoogleRequest(
    val idToken: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null
)