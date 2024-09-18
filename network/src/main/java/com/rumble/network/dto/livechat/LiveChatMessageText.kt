package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatMessageText(
    @SerializedName("text")
    val text: String
)
