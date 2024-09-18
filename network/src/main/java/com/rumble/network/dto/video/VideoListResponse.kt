package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class VideoListResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: VideoData
)