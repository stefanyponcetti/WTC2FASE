package com.example.wtcapp.data.models

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("id") val id: String,
    @SerializedName("chatId") val chatId: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("text") val text: String,
    @SerializedName(value = "sentAt", alternate = ["sentDate"]) val sentAt: String
)
