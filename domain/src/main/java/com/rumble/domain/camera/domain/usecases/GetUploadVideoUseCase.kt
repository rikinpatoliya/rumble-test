package com.rumble.domain.camera.domain.usecases

import com.rumble.domain.camera.UploadVideoEntity
import com.rumble.domain.camera.model.repository.CameraRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUploadVideoUseCase @Inject constructor(
    private val cameraRepository: CameraRepository,
) {
    operator fun invoke() = cameraRepository.getVideosList().map {
        it.map { uploadVideoData ->
            UploadVideoEntity(
                uuid = uploadVideoData.uploadUUID,
                title = uploadVideoData.title,
                status = uploadVideoData.status,
                thumbnail = uploadVideoData.tempThumbUrl ?: "",
                progress = uploadVideoData.progress,
                userWasNotifiedAboutStatus = uploadVideoData.userWasNotifiedAboutStatus,
                statusNotificationMessage = uploadVideoData.errorMessage
            )
        }
    }

    suspend operator fun invoke(uuid: String) = cameraRepository.getUploadVideo(uuid)
}