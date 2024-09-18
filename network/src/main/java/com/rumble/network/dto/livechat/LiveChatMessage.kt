package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatMessage(
    @SerializedName("id")
    val id: Long,
    @SerializedName("time")
    val time: String,
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("text")
    val text: String,
    @SerializedName("blocks")
    val blocks: List<LiveChatBlock>,
    @SerializedName("rant")
    val rant: LiveChatRant?,
    @SerializedName("notification")
    val notification: LiveChatNotification?,
    @SerializedName("channel_id")
    val channelId: Long?
)
