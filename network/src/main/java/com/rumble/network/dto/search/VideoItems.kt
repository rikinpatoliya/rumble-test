package com.rumble.network.dto.search

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.video.Video

data class VideoItems(
    @SerializedName("items")
    val items: List<Video>
)
