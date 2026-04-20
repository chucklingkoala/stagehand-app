package com.chucklingkoala.stagehand.data.remote

import com.chucklingkoala.stagehand.data.local.TokenManager
import com.google.gson.Gson
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route

class TokenRefreshAuthenticator(private val tokenManager: TokenManager) : Authenticator {

    private val gson = Gson()
    private val json = "application/json".toMediaType()

    // Plain client with no auth interceptors to avoid circular dependency
    private val plainClient = OkHttpClient()

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = tokenManager.getRefreshToken() ?: run {
            tokenManager.emitAuthFailure()
            return null
        }

        val refreshed = tryRefresh(refreshToken)
        return if (refreshed != null) {
            response.request.newBuilder()
                .header("Authorization", "Bearer ${refreshed.accessToken}")
                .build()
        } else {
            tokenManager.clearTokens()
            tokenManager.emitAuthFailure()
            null
        }
    }

    private fun tryRefresh(refreshToken: String): RefreshResult? {
        return try {
            val body = gson.toJson(mapOf("refreshToken" to refreshToken))
                .toRequestBody(json)
            val serverUrl = tokenManager.getServerUrl().trimEnd('/')
            val request = Request.Builder()
                .url("$serverUrl/api/auth/refresh")
                .post(body)
                .build()

            val resp = plainClient.newCall(request).execute()
            if (!resp.isSuccessful) return null

            val raw = resp.body?.string() ?: return null
            val parsed = gson.fromJson(raw, Map::class.java)
            val access = parsed["accessToken"] as? String ?: return null
            val refresh = parsed["refreshToken"] as? String ?: return null

            tokenManager.saveTokens(access, refresh)
            RefreshResult(access)
        } catch (e: Exception) {
            null
        }
    }

    private data class RefreshResult(val accessToken: String)
}
