package com.chucklingkoala.stagehand.data.repository

import com.chucklingkoala.stagehand.data.remote.api.StagehandApi
import com.chucklingkoala.stagehand.domain.model.Episode
import com.chucklingkoala.stagehand.domain.model.toDomain

class EpisodeRepository(private val api: StagehandApi) {

    suspend fun getEpisodes(): Result<List<Episode>> {
        return try {
            val response = api.getEpisodes()
            val episodes = response.map { it.toDomain() }.sortedByDescending { it.episodeNumber }
            Result.success(episodes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
