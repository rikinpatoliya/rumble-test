package com.rumble.domain.camera.domain.usecases

import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.uploadmanager.dto.UploadVideoData
import javax.inject.Inject

class UpdateUploadVideoProgressUseCase @Inject constructor(
    private val saveVideoUseCase: SaveVideoUseCase,
) {

    suspend operator fun invoke(
        uploadVideoData: UploadVideoData,
        status: UploadStatus,
        progress: Float,
        errorMessage: String? = null,
        uploadedThumbRef: String? = null,
        uploadedVideoRef: String? = null
    ) = saveVideoUseCase(
        uploadVideoData.copy(
            status = status,
            progress = progress,
            errorMessage = errorMessage,
            uploadedThumbRef = uploadedThumbRef ?: uploadVideoData.uploadedThumbRef,
            uploadedVideoRef = uploadedVideoRef ?: uploadVideoData.uploadedVideoRef
        )
    )
}