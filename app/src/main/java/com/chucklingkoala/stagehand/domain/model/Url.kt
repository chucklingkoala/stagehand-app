package com.chucklingkoala.stagehand.domain.model

import com.chucklingkoala.stagehand.data.remote.dto.UrlDto

data class Url(
    val id: Int,
    val url: String,
    val postedBy: String,
    val postedAt: String,
    val title: String?,
    val discordMessageId: String,
    val discordMessageLink: String?,
    val discordUserId: String?,
    val twitterUsername: String?,
    val categoryId: Int?,
    val episodeId: Int?,
    val status: UrlStatus?,
    val covered: Boolean,
    val displayOrder: Int,
    val createdAt: String,
    val updatedAt: String,
    val categoryName: String?,
    val episodeNumber: Int?,
    val isDuplicate: Boolean
)

fun UrlDto.toDomain(): Url = Url(
    id = id,
    url = url,
    postedBy = postedBy,
    postedAt = postedAt,
    title = title,
    discordMessageId = discordMessageId,
    discordMessageLink = discordMessageLink,
    discordUserId = discordUserId,
    twitterUsername = twitterUsername,
    categoryId = categoryId,
    episodeId = episodeId,
    status = UrlStatus.fromString(status),
    covered = covered,
    displayOrder = displayOrder,
    createdAt = createdAt,
    updatedAt = updatedAt,
    categoryName = categoryName,
    episodeNumber = episodeNumber,
    isDuplicate = isDuplicate
)
