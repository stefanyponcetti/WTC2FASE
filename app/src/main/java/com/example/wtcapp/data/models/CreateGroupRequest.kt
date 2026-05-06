package com.example.wtcapp.data.models

import com.google.gson.annotations.SerializedName

data class CreateGroupRequest(
    @SerializedName("name") val name: String,
    @SerializedName("members") val members: List<String>
)
