package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class Urls(
    @SerializedName("channel")
    val channelUrl: String,
    @SerializedName("video")
    val videoUrl: String
)