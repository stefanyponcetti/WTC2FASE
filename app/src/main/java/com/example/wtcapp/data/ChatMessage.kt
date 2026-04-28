package com.example.wtcapp.data

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    @SerializedName("id") val id: String,
    @SerializedName("chatId") val chatId: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("text") val text: String,
    @SerializedName("sentAt") val sentAt: String
)