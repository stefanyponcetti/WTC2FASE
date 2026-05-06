package com.example.wtcapp.data

data class UsuarioDto(
    val id: String,
    val nome: String,
    val email: String,
    val telefone: String,
    val dataNascimento: String?,
    val cargo: String?,
    val unidade: String?
)