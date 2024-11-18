package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

data class FacebookLoginResponse(
    @SerializedName("return")
    val returnData: FacebookReturnData
)