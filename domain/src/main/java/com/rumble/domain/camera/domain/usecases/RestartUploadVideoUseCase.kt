package com.rumble.domain.camera.domain.usecases

import com.rumble.domain.camera.model.repository.CameraRepository
import javax.inject.Inject

class RestartUploadVideoUseCase @Inject constructor(
    private val cameraRepository: CameraRepository
) {
    suspend operator fun invoke(uuid: String, forcedOverCellular: Boolean = false) = cameraRepository.restartVideoUpload(uuid, forcedOverCellular)
}