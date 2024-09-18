package com.rumble.network.dto.discover

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class CategoryResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: CategoryData
)

data class CategoryData(
    @SerializedName("category")
    val category: Category,
    @SerializedName("items")
    val items: List<Category>
)
