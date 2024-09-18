package com.rumble.network.dto.settings

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class AuthProvidersResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: AuthProviders
)