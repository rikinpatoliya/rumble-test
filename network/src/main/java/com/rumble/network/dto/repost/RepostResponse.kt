package com.rumble.network.dto.repost

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class RepostResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: Repost,
)