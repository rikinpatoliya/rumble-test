package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatDeleteMessagesData(
    @SerializedName("message_ids")
    val messageIdList: List<Long>
)