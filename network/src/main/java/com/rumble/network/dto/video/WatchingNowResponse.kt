package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class WatchingNowResponse(
    @SerializedName("data")
    val data: WatchingNowData
)
