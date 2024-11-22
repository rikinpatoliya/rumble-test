package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.creator.Creator

data class ChannelItems(
    @SerializedName("items")
    val items: List<Creator>
)
