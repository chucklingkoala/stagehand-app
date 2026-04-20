package com.chucklingkoala.stagehand.data.repository

import com.chucklingkoala.stagehand.data.local.TokenManager
import com.chucklingkoala.stagehand.data.remote.api.StagehandApi
import com.chucklingkoala.stagehand.data.remote.dto.LoginRequest

class AuthRepository(
    private val api: StagehandApi,
    private val tokenManager: TokenManager
) {

    suspend fun login(serverUrl: String, username: String, password: String): Result<Unit> {
        return try {
            tokenManager.saveServerUrl(serverUrl)
            val response = api.login(LoginRequest(username = username, password = password))
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.clearTokens()
    }
}
