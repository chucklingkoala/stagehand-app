package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BulkOperationRequest(
    @SerializedName("url_ids") val urlIds: List<Int>,
    @SerializedName("operation") val operation: String, // "categorize" or "flag"
    @SerializedName("value") val value: Any // Int for category_id, String for status
)

data class BulkOperationResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("affected") val affected: Int
)
