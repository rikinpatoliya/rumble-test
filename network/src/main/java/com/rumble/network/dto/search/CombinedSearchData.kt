package com.rumble.network.dto.search

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.channel.ChannelItems

data class CombinedSearchData(
    @SerializedName("channel")
    val channel: ChannelItems,
    @SerializedName("video")
    val video: VideoItems
)
