package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatMessageBody(
    @SerializedName("data")
    val bodyData: LiveChatBodyData
)

data class LiveChatBodyData(
    @SerializedName("request_id")
    val requestId: String,
    @SerializedName("message")
    val message: LiveChatMessageText,
    @SerializedName("channel_id")
    val channelId: Long? = null,
    @SerializedName("rant")
    val rant: LiveChatMessageRant? = null
)

data class LiveChatMessageRant(
    @SerializedName("level_id")
    val level: String,
    @SerializedName("platform")
    val platform: String = androidPlatform
)
