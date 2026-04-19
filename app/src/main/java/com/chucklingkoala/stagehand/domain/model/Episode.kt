package com.chucklingkoala.stagehand.domain.model

import com.chucklingkoala.stagehand.data.remote.dto.EpisodeDto

data class Episode(
    val id: Int,
    val episodeNumber: Int,
    val showNotes: String?,
    val urlCount: Int,
    val onShowCount: Int,
    val coveredCount: Int,
    val createdAt: String,
    val updatedAt: String
)

fun EpisodeDto.toDomain(): Episode = Episode(
    id = id,
    episodeNumber = episodeNumber,
    showNotes = showNotes,
    urlCount = urlCount,
    onShowCount = onShowCount,
    coveredCount = coveredCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)
