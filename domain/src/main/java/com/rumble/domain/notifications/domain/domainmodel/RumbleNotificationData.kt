package com.rumble.domain.notifications.domain.domainmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val KEY_NOTIFICATION_VIDEO_DETAILS = "videoDetailsNotification"

@Parcelize
data class RumbleNotificationData(
    val guid: String,
    val videoDetailsNotificationData: VideoDetailsNotificationData?,
) : Parcelable

@Parcelize
data class VideoDetailsNotificationData(
    val id: String?,
    val url: String?,
) : Parcelable