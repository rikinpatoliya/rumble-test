package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class FollowedChannelsResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: FollowedChannelItems
)

data class FollowedChannelItems(
    @SerializedName("items")
    val items: List<Channel>?
)
