package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class ChannelsResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: ChannelItems
)
