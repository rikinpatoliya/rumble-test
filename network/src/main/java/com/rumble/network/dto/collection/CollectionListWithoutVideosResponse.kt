package com.rumble.network.dto.collection

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class CollectionListWithoutVideosResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: CollectionDataWithoutVideos,
)