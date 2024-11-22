package com.rumble.network.dto.repost

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.creator.UserLight

data class RepostListResponse(
    @SerializedName("user")
    val user: UserLight,
    @SerializedName("data")
    val data: RepostListData,
)

data class RepostListData(
    @SerializedName("items")
    val items: List<Repost>
)
