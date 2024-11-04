package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveGate(
    @SerializedName("timecode")
    val timeCode: Long,
    @SerializedName("countdown_seconds")
    val countdown: Int,
    @SerializedName("chat_mode")
    val chatMode: Int,
)
