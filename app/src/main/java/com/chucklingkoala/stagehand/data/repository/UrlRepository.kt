package com.chucklingkoala.stagehand.data.repository

import com.chucklingkoala.stagehand.data.remote.api.StagehandApi
import com.chucklingkoala.stagehand.data.remote.dto.BulkOperationRequest
import com.chucklingkoala.stagehand.data.remote.dto.SubmitUrlRequest
import com.chucklingkoala.stagehand.domain.model.*

class UrlRepository(private val api: StagehandApi) {

    suspend fun getUrls(
        limit: Int = 50,
        offset: Int = 0,
        categoryId: Int? = null,
        episodeId: Int? = null,
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
                episodeId = episodeId,
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

    private suspend fun patch(id: Int, fields: Map<String, Any?>): Result<Url> {
        return try {
            val response = api.updateUrl(id, fields)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStatus(id: Int, status: String?): Result<Url> =
        patch(id, mapOf("status" to status))

    suspend fun updateCategory(id: Int, categoryId: Int?): Result<Url> =
        patch(id, mapOf("category_id" to categoryId))

    suspend fun updateEpisode(id: Int, episodeId: Int?): Result<Url> =
        patch(id, mapOf("episode_id" to episodeId))

    suspend fun updateCovered(id: Int, covered: Boolean): Result<Url> =
        patch(id, mapOf("covered" to covered))

    suspend fun submitUrl(url: String, title: String?): Result<Url> {
        return try {
            val response = api.submitUrl(SubmitUrlRequest(url = url, title = title))
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bulkCategorize(urlIds: List<Int>, categoryId: Int?): Result<Int> =
        bulk(urlIds, operation = "categorize", value = categoryId ?: 0)

    suspend fun bulkAssignEpisode(urlIds: List<Int>, episodeId: Int?): Result<Int> =
        bulk(urlIds, operation = "assign_episode", value = episodeId ?: 0)

    suspend fun bulkFlag(urlIds: List<Int>, status: String): Result<Int> =
        bulk(urlIds, operation = "flag", value = status)

    suspend fun bulkMarkCovered(urlIds: List<Int>, covered: Boolean): Result<Int> =
        bulk(urlIds, operation = "mark_covered", value = covered)

    suspend fun bulkDelete(urlIds: List<Int>): Result<Int> =
        bulk(urlIds, operation = "delete", value = 0)

    private suspend fun bulk(urlIds: List<Int>, operation: String, value: Any): Result<Int> {
        return try {
            val response = api.bulkOperation(
                BulkOperationRequest(urlIds = urlIds, operation = operation, value = value)
            )
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
