package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VersionDto(
    @SerializedName("version") val version: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String
)
