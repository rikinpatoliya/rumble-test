package com.rumble.domain.notifications.domain.domainmodel

import com.google.gson.annotations.SerializedName

data class RumbleOneSignalNotificationData(
    @SerializedName("destination")
    val destination: String? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("url-args")
    val url: String? = null,
)