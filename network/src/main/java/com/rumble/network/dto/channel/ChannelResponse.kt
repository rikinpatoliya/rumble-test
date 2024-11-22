package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.creator.Creator
import com.rumble.network.dto.login.UserState

data class ChannelResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: Creator
)