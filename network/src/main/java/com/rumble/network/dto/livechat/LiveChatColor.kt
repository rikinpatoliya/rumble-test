package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatColor(
    @SerializedName("fg")
    val fg: String,
    @SerializedName("main")
    val main: String,
    @SerializedName("bg2")
    val bg2: String
)
