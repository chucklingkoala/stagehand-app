package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaginatedUrlResponse(
    @SerializedName("urls") val urls: List<UrlDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("offset") val offset: Int
)
