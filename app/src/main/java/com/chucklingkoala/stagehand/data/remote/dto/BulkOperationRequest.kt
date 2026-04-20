package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class BulkOperationRequest(
    @SerializedName("url_ids") val urlIds: List<Int>,
    @SerializedName("operation") val operation: String,
    @SerializedName("value") val value: JsonElement
)

data class BulkOperationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("affected") val affected: Int
)
