package com.chucklingkoala.stagehand.data.local

import android.content.Context
import com.chucklingkoala.stagehand.BuildConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TokenManager(context: Context) {

    private val prefs = context.getSharedPreferences("stagehand_prefs", Context.MODE_PRIVATE)

    private val _authFailureEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val authFailureEvent: SharedFlow<Unit> = _authFailureEvent.asSharedFlow()

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun saveServerUrl(url: String) {
        prefs.edit().putString(KEY_SERVER_URL, url.trimEnd('/')).apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getServerUrl(): String =
        prefs.getString(KEY_SERVER_URL, null) ?: BuildConfig.API_BASE_URL.trimEnd('/')

    fun clearTokens() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    fun isLoggedIn(): Boolean = getAccessToken() != null

    fun emitAuthFailure() {
        _authFailureEvent.tryEmit(Unit)
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_SERVER_URL = "server_url"
    }
}
