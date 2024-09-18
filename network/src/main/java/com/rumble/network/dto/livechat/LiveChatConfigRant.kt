package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatConfigRant(
    @SerializedName("levels")
    val levels: List<LiveChatLevel>,
    @SerializedName("enable")
    val enable: Boolean?
)
