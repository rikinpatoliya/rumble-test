package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LivePinMessageData(
    @SerializedName("message")
    val message: LiveChatMessage
)