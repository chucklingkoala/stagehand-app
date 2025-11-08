package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LinkPreviewDto(
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("image_url") val imageUrl: String?
)
