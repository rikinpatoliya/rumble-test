package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatChannel(
    @SerializedName("id")
    val id: Long,
    @SerializedName("username")
    val username: String,
    @SerializedName("image.1")
    val image: String?,
)