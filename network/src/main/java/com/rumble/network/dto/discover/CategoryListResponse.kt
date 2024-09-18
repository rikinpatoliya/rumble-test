package com.rumble.network.dto.discover

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class CategoryListResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: CategoryListData
)

data class CategoryListData(
    @SerializedName("items")
    val items: List<Category>
)
