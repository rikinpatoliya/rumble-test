package com.rumble.network.dto.repost

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.video.User

data class RepostListResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("data")
    val data: RepostListData,
)

data class RepostListData(
    @SerializedName("items")
    val items: List<Repost>
)
