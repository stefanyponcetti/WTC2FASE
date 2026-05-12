package com.example.wtcapp.data.models

import com.google.gson.annotations.SerializedName

data class MessageStatusUpdate(
    @SerializedName("messageId") val messageId: String,
    @SerializedName("status") val status: String
)
