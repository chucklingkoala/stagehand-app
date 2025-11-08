package com.chucklingkoala.stagehand.data.repository

import com.chucklingkoala.stagehand.data.remote.api.StagehandApi
import com.chucklingkoala.stagehand.data.remote.dto.CreateCategoryRequest
import com.chucklingkoala.stagehand.data.remote.dto.UpdateCategoryRequest
import com.chucklingkoala.stagehand.domain.model.Category
import com.chucklingkoala.stagehand.domain.model.toDomain

class CategoryRepository(private val api: StagehandApi) {

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val response = api.getCategories()
            val categories = response.map { it.toDomain() }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategory(id: Int): Result<Category> {
        return try {
            val response = api.getCategory(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createCategory(
        name: String,
        color: String
    ): Result<Category> {
        return try {
            val request = CreateCategoryRequest(name, color)
            val response = api.createCategory(request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(
        id: Int,
        name: String,
        color: String
    ): Result<Category> {
        return try {
            val request = UpdateCategoryRequest(name, color)
            val response = api.updateCategory(id, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(id: Int): Result<Unit> {
        return try {
            api.deleteCategory(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
