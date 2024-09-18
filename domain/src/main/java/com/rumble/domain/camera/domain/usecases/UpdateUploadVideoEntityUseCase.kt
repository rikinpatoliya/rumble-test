package com.rumble.domain.camera.domain.usecases

import com.rumble.domain.camera.UploadVideoEntity
import com.rumble.domain.camera.model.repository.CameraRepository
import javax.inject.Inject

class UpdateUploadVideoEntityUseCase @Inject constructor(
    private val cameraRepository: CameraRepository,
) {
    suspend operator fun invoke(uploadVideoEntity: UploadVideoEntity) {
        cameraRepository.getUploadVideo(uploadVideoEntity.uuid)?.let { uploadVideoData ->
            cameraRepository.saveVideo(
                uploadVideoData.copy(
                    userWasNotifiedAboutStatus = uploadVideoEntity.userWasNotifiedAboutStatus
                )
            )
        }
    }
}