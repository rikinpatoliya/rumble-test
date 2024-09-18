package com.rumble.network.dto.discover

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState
import com.rumble.network.dto.video.Video

data class CategoryVideoListResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: CategoryVideoListData
)

data class CategoryVideoListData(
    @SerializedName("items")
    val items: List<Video>?
)
