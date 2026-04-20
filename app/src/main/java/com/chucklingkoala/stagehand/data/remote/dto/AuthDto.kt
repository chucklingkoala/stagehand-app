package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class RefreshRequest(
    @SerializedName("refreshToken") val refreshToken: String
)

data class RefreshResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)
