package com.example.wtcapp.data.models

data class UpdateUsuarioDto(
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val dataNascimento: String?,
    val unidade: String
)
