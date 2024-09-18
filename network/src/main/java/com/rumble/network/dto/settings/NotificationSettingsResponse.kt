package com.rumble.network.dto.settings

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class NotificationSettingsResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: NotificationSettings
)