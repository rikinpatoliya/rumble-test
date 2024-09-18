package com.rumble.domain.discover.model.datasource

import com.rumble.network.api.DiscoverApi
import com.rumble.network.dto.discover.CategoryListResponse
import com.rumble.network.dto.discover.CategoryResponse
import retrofit2.Response

class CategoryDataSourceImpl(private val discoverApi: DiscoverApi) : CategoryDataSource {

    override suspend fun fetchCategoryList(limit: Int?): Response<CategoryListResponse> =
        discoverApi.fetchCategoryList(limit)

    override suspend fun fetchCategory(categoryName: String): Response<CategoryResponse> =
        discoverApi.fetchCategory(categoryName)
}