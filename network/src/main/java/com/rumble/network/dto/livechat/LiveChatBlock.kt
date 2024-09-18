package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatBlock(
    @SerializedName("type")
    val type: String,
    @SerializedName("data")
    val blockData: LiveChatBlockData
)