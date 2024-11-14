package com.rumble.domain.uploadmanager.dto

import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.settings.domain.domainmodel.UploadQuality

data class UploadVideoData(
    val uploadUUID: String,
    val videoUri: String,
    val videoExtension: String,
    val title: String = "",
    val description: String = "",
    val tags: String = "",
    val licence: Int = 0,
    val rightsAccepted: Boolean = false,
    val termsAccepted: Boolean = false,
    val channelId: Long = 0,
    val infoWho: String? = null,
    val infoWhere: String? = null,
    val infoExtUser: String? = null,
    val visibility: String,
    val publishDate: Long? = null,
    val status: UploadStatus = UploadStatus.DRAFT,
    val progress: Float = 0F,
    val errorMessage: String? = null,
    val trimStart: Float? = null,
    val trimEnd: Float? = null,
    val uploadQuality: UploadQuality = UploadQuality.defaultUploadQuality,
    val tempThumbUrl: String? = null,
    val tempVideoUrl: String? = null,
    val uploadedThumbRef: String? = null,
    val uploadedVideoRef: String? = null,
    val userWasNotifiedAboutStatus: Boolean = false,
    val siteChannelId: Int? = null,
    val mediaChannelId: Int? = null
)
