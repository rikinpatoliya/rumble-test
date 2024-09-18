package com.rumble.network.dto.livechatevents

import com.google.gson.annotations.SerializedName

data class MessageEventResponse(
    @SerializedName("data")
    val data: MessageEventResultData
)

data class MessageEventResultData(
    @SerializedName("success")
    val success: Boolean
)
