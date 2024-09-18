package com.rumble.domain.discover.model.datasource

import com.rumble.network.dto.discover.CategoryListResponse
import com.rumble.network.dto.discover.CategoryResponse
import retrofit2.Response

interface CategoryDataSource {
    suspend fun fetchCategoryList(limit: Int?): Response<CategoryListResponse>
    suspend fun fetchCategory(categoryName: String): Response<CategoryResponse>
}