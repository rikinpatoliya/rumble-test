package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

data class ResetPasswordResponse(
    @SerializedName("data")
    val data: ResetPasswordResponseData,
)

data class ResetPasswordResponseData(
    @SerializedName("error")
    val error: String?,
)