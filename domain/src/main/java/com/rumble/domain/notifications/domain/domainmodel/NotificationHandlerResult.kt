package com.rumble.domain.notifications.domain.domainmodel

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity

sealed class NotificationHandlerResult {

    object UnhandledNotificationData : NotificationHandlerResult()

    data class VideoDetailsNotificationData(
        val success: Boolean,
        val videoEntity: VideoEntity?
    ) : NotificationHandlerResult()
}
