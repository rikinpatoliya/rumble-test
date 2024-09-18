package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class PlayListVideoData(
    @SerializedName("items")
    val items: List<PlayListItem>
)