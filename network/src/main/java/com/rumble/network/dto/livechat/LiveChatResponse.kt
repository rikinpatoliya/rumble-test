package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatResponse(
    @SerializedName("data")
    val data: ResponseData
)

data class ResponseData(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("pending_message_id")
    val pendingMessageId: Long?
)
