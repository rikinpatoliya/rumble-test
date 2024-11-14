package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

data class IdpLoginResponse(
    @SerializedName("user")
    val user: UserState?,
    @SerializedName("data")
    val userData: UserData
)
