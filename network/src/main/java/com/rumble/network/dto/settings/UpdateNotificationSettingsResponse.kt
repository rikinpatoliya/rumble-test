package com.rumble.network.dto.settings

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class UpdateNotificationSettingsResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: UpdateNotificationsDataResponse
)

data class UpdateNotificationsDataResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("error")
    val error: String?,
    @SerializedName("message")
    val message: String?,
)