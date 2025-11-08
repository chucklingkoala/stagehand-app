package com.chucklingkoala.stagehand.data.repository

import com.chucklingkoala.stagehand.data.remote.api.StagehandApi
import com.chucklingkoala.stagehand.data.remote.dto.BulkOperationRequest
import com.chucklingkoala.stagehand.data.remote.dto.UpdateUrlRequest
import com.chucklingkoala.stagehand.domain.model.*

class UrlRepository(private val api: StagehandApi) {

    suspend fun getUrls(
        limit: Int = 50,
        offset: Int = 0,
        categoryId: Int? = null,
        status: String? = null,
        search: String? = null,
        covered: Boolean? = null,
        postedBy: String? = null,
        sort: String = "desc"
    ): Result<Pair<List<Url>, Int>> {
        return try {
            val response = api.getUrls(
                limit = limit,
                offset = offset,
                categoryId = categoryId,
                status = status,
                search = search,
                covered = covered,
                postedBy = postedBy,
                sort = sort
            )
            val urls = response.urls.map { it.toDomain() }
            Result.success(Pair(urls, response.total))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUrl(id: Int): Result<Url> {
        return try {
            val response = api.getUrl(id)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUrl(
        id: Int,
        categoryId: Int? = null,
        status: String? = null,
        covered: Boolean? = null,
        displayOrder: Int? = null
    ): Result<Url> {
        return try {
            val request = UpdateUrlRequest(
                categoryId = categoryId,
                status = status,
                covered = covered,
                displayOrder = displayOrder
            )
            val response = api.updateUrl(id, request)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bulkCategorize(
        urlIds: List<Int>,
        categoryId: Int?
    ): Result<Int> {
        return try {
            val request = BulkOperationRequest(
                urlIds = urlIds,
                operation = "categorize",
                value = categoryId ?: 0
            )
            val response = api.bulkOperation(request)
            Result.success(response.affected)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bulkFlag(
        urlIds: List<Int>,
        status: String
    ): Result<Int> {
        return try {
            val request = BulkOperationRequest(
                urlIds = urlIds,
                operation = "flag",
                value = status
            )
            val response = api.bulkOperation(request)
            Result.success(response.affected)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLinkPreview(url: String): Result<LinkPreview> {
        return try {
            val response = api.getLinkPreview(url, fetch = true)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
