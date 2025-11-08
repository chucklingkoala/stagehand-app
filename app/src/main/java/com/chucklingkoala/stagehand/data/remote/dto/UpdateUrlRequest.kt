package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateUrlRequest(
    @SerializedName("category_id") val categoryId: Int? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("display_order") val displayOrder: Int? = null,
    @SerializedName("covered") val covered: Boolean? = null
)
