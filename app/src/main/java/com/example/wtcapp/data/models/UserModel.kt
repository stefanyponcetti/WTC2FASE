package com.example.wtcapp.data.models

import com.google.gson.annotations.SerializedName

data class UserModel(
    @SerializedName("id") val id: String,
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("tipoCliente") val tipoCliente: String
) {
    val isExternal: Boolean
        get() = tipoCliente.lowercase() != "interno"
}
