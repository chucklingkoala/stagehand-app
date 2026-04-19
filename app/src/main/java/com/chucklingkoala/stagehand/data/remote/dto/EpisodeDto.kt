package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EpisodeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("episode_number") val episodeNumber: Int,
    @SerializedName("show_notes") val showNotes: String?,
    @SerializedName("url_count") val urlCount: Int = 0,
    @SerializedName("on_show_count") val onShowCount: Int = 0,
    @SerializedName("covered_count") val coveredCount: Int = 0,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
