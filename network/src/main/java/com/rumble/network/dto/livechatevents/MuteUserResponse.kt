package com.rumble.network.dto.livechatevents

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class MuteUserResponse(
    @SerializedName("data")
    val data: MuteUserData
)

data class MuteUserData(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("user")
    val userState: UserState
)