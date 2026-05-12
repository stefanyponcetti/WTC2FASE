package com.example.wtcapp.data.models

import com.google.gson.annotations.SerializedName

data class ComunicadoNotificacao(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("autor") val autor: String,
    @SerializedName("destinatarios") val destinatarios: String
)
