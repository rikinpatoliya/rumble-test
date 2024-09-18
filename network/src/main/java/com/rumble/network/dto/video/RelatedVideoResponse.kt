package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class RelatedVideoResponse(
    @SerializedName("user")
    val userState: UserState,
    @SerializedName("data")
    val videoData: RelatedVideoData
)