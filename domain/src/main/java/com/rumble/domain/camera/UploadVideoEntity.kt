package com.rumble.domain.camera

data class UploadVideoEntity(
    val uuid: String,
    val title: String = "",
    val status: UploadStatus = UploadStatus.DRAFT,
    val thumbnail: String = "",
    val progress: Float = 0F,
    val userWasNotifiedAboutStatus: Boolean = false,
    val statusNotificationMessage: String? = null
)
