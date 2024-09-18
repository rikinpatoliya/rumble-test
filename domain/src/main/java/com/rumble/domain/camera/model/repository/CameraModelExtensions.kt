package com.rumble.domain.camera.model.repository

import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.model.datasource.local.RoomVideo
import com.rumble.domain.settings.domain.domainmodel.UploadQuality
import com.rumble.domain.uploadmanager.dto.UploadVideoData
import java.time.Instant

fun UploadVideoData.getRoomVideo() =
    RoomVideo(
        uuid = uploadUUID,
        videoUrl = videoUri,
        videoExtension = videoExtension,
        title = title,
        description = description,
        tags = tags,
        licence = licence,
        rights = rightsAccepted,
        terms = termsAccepted,
        channelId = channelId,
        infoWho = infoWho,
        infoWhere = infoWhere,
        infoExtUser = infoExtUser,
        visibility = visibility,
        status = status.value,
        publishDate = publishDate,
        errorMessage = errorMessage,
        progress = progress,
        trimStart = trimStart,
        trimEnd = trimEnd,
        uploadQuality = uploadQuality.value,
        tempThumbUrl = tempThumbUrl,
        tempVideoUrl = tempVideoUrl,
        uploadedThumbRef = uploadedThumbRef,
        uploadedVideoRef = uploadedVideoRef,
        userNotifiedAboutStatus = userWasNotifiedAboutStatus
    )

fun RoomVideo.getUploadVideoData() =
    UploadVideoData(
        uploadUUID = uuid,
        videoUri = videoUrl,
        videoExtension = videoExtension,
        title = title,
        description = description,
        tags = tags,
        licence = licence,
        rightsAccepted = rights,
        termsAccepted = terms,
        channelId = channelId,
        infoWho = infoWho,
        infoWhere = infoWhere,
        infoExtUser = infoExtUser,
        visibility = visibility,
        publishDate = publishDate,
        status = UploadStatus.getByValue(status),
        progress = progress,
        errorMessage = errorMessage,
        trimStart = trimStart,
        trimEnd = trimEnd,
        uploadQuality = UploadQuality.getByValue(uploadQuality),
        tempThumbUrl = tempThumbUrl,
        tempVideoUrl = tempVideoUrl,
        uploadedThumbRef = uploadedThumbRef,
        uploadedVideoRef = uploadedVideoRef,
        userWasNotifiedAboutStatus = userNotifiedAboutStatus
    )