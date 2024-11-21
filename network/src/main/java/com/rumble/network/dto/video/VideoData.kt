package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class VideoData(
    @SerializedName("items")
    val items: List<FeedItem>
)
