package com.rumble.network.dto.profile

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class GetUnreadNotificationsResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: UnreadNotificationsData
)

data class UnreadNotificationsData(
    @SerializedName("has_unread_notifications")
    val hasUnreadNotifications: Boolean
)