package com.rumble.domain.profile.domainmodel

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import java.time.LocalDateTime

data class ProfileNotificationEntity(
    val userName: String,
    val userThumb: String,
    val channelId: String,
    val message: String,
    val timeAgo: LocalDateTime,
    val videoEntity: VideoEntity?
)