package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class RelatedVideoData(
    @SerializedName("videos")
    val items: List<Video>
)
