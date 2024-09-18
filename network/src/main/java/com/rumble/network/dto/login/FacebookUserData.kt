package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

data class FacebookUserData(
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("username")
    val userName: String?
)
