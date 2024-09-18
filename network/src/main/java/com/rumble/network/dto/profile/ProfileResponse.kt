package com.rumble.network.dto.profile

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class ProfileResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: UserProfile,
)