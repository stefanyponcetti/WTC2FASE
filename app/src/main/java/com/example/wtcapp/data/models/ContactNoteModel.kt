package com.example.wtcapp.data.models

import com.google.gson.annotations.SerializedName

data class ContactNoteResponse(
    @SerializedName("contactId") val contactId: String,
    @SerializedName("note") val note: String,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class UpsertNoteRequest(
    @SerializedName("note") val note: String
)
