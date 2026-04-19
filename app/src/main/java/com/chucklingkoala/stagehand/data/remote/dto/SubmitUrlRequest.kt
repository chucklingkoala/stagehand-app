package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SubmitUrlRequest(
    @SerializedName("url") val url: String,
    @SerializedName("title") val title: String? = null
)
