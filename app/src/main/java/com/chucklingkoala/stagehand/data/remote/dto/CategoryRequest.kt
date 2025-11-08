package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateCategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String
)

data class UpdateCategoryRequest(
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String
)
