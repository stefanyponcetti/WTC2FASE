package com.example.wtcapp.data.models

import com.google.gson.annotations.SerializedName

data class ChatModel(
    @SerializedName("id") val id: String,
    @SerializedName("isGroup") val isGroup: Boolean,
    @SerializedName("groupName") val groupName: String?,
    @SerializedName("members") val members: List<String>? = null,
    @SerializedName("lastMessage") val lastMessage: LastMessageData? = null
)

data class LastMessageData(
    @SerializedName("text") val text: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("sentDate") val sentDate: String
)

fun ChatModel.displayName(currentUserId: String, userNames: Map<String, String>): String {
    if (isGroup) return groupName ?: "Grupo"

    val otherId = members
        ?.firstOrNull { memberId -> memberId != currentUserId }
        ?: return "Chat privado"

    return userNames[otherId] ?: "Chat privado"
}
