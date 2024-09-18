package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class WatchingProgress(
    @SerializedName("last_time")
    val lastPosition: Long
)