package com.chucklingkoala.stagehand.data.remote.api

import com.chucklingkoala.stagehand.data.remote.dto.*
import retrofit2.http.*

interface StagehandApi {

    @GET("urls")
    suspend fun getUrls(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("category_id") categoryId: Int? = null,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null,
        @Query("covered") covered: Boolean? = null,
        @Query("posted_by") postedBy: String? = null,
        @Query("sort") sort: String = "desc"
    ): PaginatedUrlResponse

    @GET("urls/{id}")
    suspend fun getUrl(
        @Path("id") id: Int
    ): UrlDto

    @PUT("urls/{id}")
    suspend fun updateUrl(
        @Path("id") id: Int,
        @Body request: UpdateUrlRequest
    ): UrlDto

    @POST("urls/bulk")
    suspend fun bulkOperation(
        @Body request: BulkOperationRequest
    ): BulkOperationResponse

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("categories/{id}")
    suspend fun getCategory(
        @Path("id") id: Int
    ): CategoryDto

    @POST("categories")
    suspend fun createCategory(
        @Body request: CreateCategoryRequest
    ): CategoryDto

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body request: UpdateCategoryRequest
    ): CategoryDto

    @DELETE("categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: Int
    )

    @GET("link-preview")
    suspend fun getLinkPreview(
        @Query("url") url: String,
        @Query("fetch") fetch: Boolean = true
    ): LinkPreviewDto

    @GET("version")
    suspend fun getVersion(): VersionDto
}
