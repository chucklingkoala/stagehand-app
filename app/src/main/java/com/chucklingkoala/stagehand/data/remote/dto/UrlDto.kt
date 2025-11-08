package com.chucklingkoala.stagehand.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UrlDto(
    @SerializedName("id") val id: Int,
    @SerializedName("url") val url: String,
    @SerializedName("posted_by") val postedBy: String,
    @SerializedName("posted_at") val postedAt: String,
    @SerializedName("title") val title: String?,
    @SerializedName("discord_message_id") val discordMessageId: String,
    @SerializedName("discord_message_link") val discordMessageLink: String?,
    @SerializedName("discord_user_id") val discordUserId: String?,
    @SerializedName("twitter_username") val twitterUsername: String?,
    @SerializedName("category_id") val categoryId: Int?,
    @SerializedName("episode_id") val episodeId: Int?,
    @SerializedName("status") val status: String?,
    @SerializedName("covered") val covered: Boolean,
    @SerializedName("display_order") val displayOrder: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("category_name") val categoryName: String?,
    @SerializedName("episode_number") val episodeNumber: Int?,
    @SerializedName("is_duplicate") val isDuplicate: Boolean
)
