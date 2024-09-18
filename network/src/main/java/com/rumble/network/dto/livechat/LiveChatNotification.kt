package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatNotification(
    @SerializedName("text")
    val text: String,
    @SerializedName("badge")
    val badge: String
)