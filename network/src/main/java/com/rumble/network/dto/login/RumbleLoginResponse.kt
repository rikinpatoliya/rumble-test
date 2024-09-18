package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

data class RumbleLoginResponse(
    @SerializedName("success")
    val success: Int = 0,
    @SerializedName("userid")
    val userId: Int? = null,
    @SerializedName("username")
    val userName: String? = null,
    @SerializedName("profilePic")
    val profilePicture: String? = null,
    @SerializedName("subscriptions_count")
    val subscriptionCount: Int? = null,
    @SerializedName("is_admin")
    val isAdmin: Boolean = false
)
