package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

data class FacebookReturnData(
    @SerializedName("success")
    val success: Any,
    @SerializedName("user")
    val userData: FacebookUserData?
)
