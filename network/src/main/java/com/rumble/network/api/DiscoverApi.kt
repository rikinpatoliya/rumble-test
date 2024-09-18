package com.rumble.network.api

import com.rumble.network.dto.discover.CategoryListResponse
import com.rumble.network.dto.discover.CategoryResponse
import com.rumble.network.dto.discover.CategoryVideoListResponse
import com.rumble.network.queryHelpers.CategoryVideoType
import com.rumble.network.queryHelpers.Options
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscoverApi {

    @GET("service.php?name=category.list")
    suspend fun fetchCategoryList(@Query("limit") limit: Int? = null): Response<CategoryListResponse>

    @GET("service.php?name=category.get")
    suspend fun fetchCategory(@Query("category") categoryName: String): Response<CategoryResponse>

    @GET("service.php?name=category.videos")
    suspend fun fetchCategoryVideoList(
        @Query("options") options: String = listOf(Options.FULL, Options.WATCHING_PROGRESS).joinToString(separator = ","),
        @Query("category") category: String,
        @Query("video_type") videoType: CategoryVideoType,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<CategoryVideoListResponse>
}