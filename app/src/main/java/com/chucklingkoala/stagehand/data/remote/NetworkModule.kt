package com.chucklingkoala.stagehand.data.remote

import com.chucklingkoala.stagehand.BuildConfig
import com.chucklingkoala.stagehand.data.local.TokenManager
import com.chucklingkoala.stagehand.data.remote.api.StagehandApi
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    fun create(tokenManager: TokenManager): StagehandApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(BaseUrlInterceptor(tokenManager))
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .authenticator(TokenRefreshAuthenticator(tokenManager))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .serializeNulls()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(StagehandApi::class.java)
    }
}
