package com.chucklingkoala.stagehand.domain.model

import com.chucklingkoala.stagehand.data.remote.dto.CategoryDto

data class Category(
    val id: Int,
    val name: String,
    val color: String,
    val createdAt: String,
    val updatedAt: String,
    val urlCount: Int
)

fun CategoryDto.toDomain(): Category = Category(
    id = id,
    name = name,
    color = color,
    createdAt = createdAt,
    updatedAt = updatedAt,
    urlCount = urlCount?.toIntOrNull() ?: 0
)
