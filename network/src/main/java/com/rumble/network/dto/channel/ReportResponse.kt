package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class ReportResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: ReportResponseData?,
)

data class ReportResponseData(
    @SerializedName("success")
    val success: Boolean
)