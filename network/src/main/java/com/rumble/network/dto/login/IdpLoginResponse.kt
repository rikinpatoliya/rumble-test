package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

data class IdpLoginResponse(
    @SerializedName("user")
    val user: UserState?,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("user_name")
    val userName: String?,
    @SerializedName("thumb")
    val thumb: String?,
    @SerializedName("error")
    val error: String?
)
