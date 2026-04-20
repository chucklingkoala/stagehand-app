package com.chucklingkoala.stagehand.data.remote

import com.chucklingkoala.stagehand.data.local.TokenManager
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val storedUrl = tokenManager.getServerUrl()
        val base = storedUrl.toHttpUrlOrNull() ?: return chain.proceed(original)

        val newUrl = original.url.newBuilder()
            .scheme(base.scheme)
            .host(base.host)
            .port(base.port)
            .build()

        return chain.proceed(original.newBuilder().url(newUrl).build())
    }
}
