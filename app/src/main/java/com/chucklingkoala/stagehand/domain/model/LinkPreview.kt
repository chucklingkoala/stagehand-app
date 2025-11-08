package com.chucklingkoala.stagehand.domain.model

import com.chucklingkoala.stagehand.data.remote.dto.LinkPreviewDto

data class LinkPreview(
    val title: String?,
    val description: String?,
    val imageUrl: String?
)

fun LinkPreviewDto.toDomain(): LinkPreview = LinkPreview(
    title = title,
    description = description,
    imageUrl = imageUrl
)
