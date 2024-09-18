package com.rumble.domain.camera.domain.usecases

import com.rumble.domain.camera.model.repository.CameraRepository
import javax.inject.Inject

class DeleteUploadVideoUseCase @Inject constructor(
    private val cameraRepository: CameraRepository,
) {
    suspend operator fun invoke(uuid: String) =
        cameraRepository.deleteVideoUpload(uuid)
}