package com.rumble.domain.camera.domain.usecases

import com.rumble.domain.camera.model.repository.CameraRepository
import com.rumble.domain.uploadmanager.dto.UploadVideoData
import javax.inject.Inject

class UploadVideoUseCase @Inject constructor(
    private val cameraRepository: CameraRepository,
) {
    suspend operator fun invoke(uploadVideoData: UploadVideoData) =
        cameraRepository.uploadVideo(uploadVideoData)
}