package com.example.wtcapp.data.models

import com.google.gson.annotations.SerializedName

data class ComunicadoModel(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("autor") val autor: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("destinatarios") val destinatarios: String,
    @SerializedName("dataValidade") val dataValidade: String,
    @SerializedName("dataCriacao") val dataCriacao: String,
    @SerializedName("podeEditar") val podeEditar: Boolean
)

data class CreateComunicadoRequest(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("destinatarios") val destinatarios: String,
    @SerializedName("dataValidade") val dataValidade: String
)
