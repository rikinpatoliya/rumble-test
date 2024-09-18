package com.rumble.domain.camera.domain.usecases

import com.rumble.domain.camera.UploadStatus
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUploadNotificationVideoUseCase @Inject constructor(
    private val getUploadVideoUseCase: GetUploadVideoUseCase,
) {
    operator fun invoke() = getUploadVideoUseCase().map { uploadList ->
        uploadList.filter {
            (it.status == UploadStatus.UPLOADING_FAILED || it.status == UploadStatus.UPLOADING_SUCCEEDED)
                    && it.userWasNotifiedAboutStatus.not()
        }
    }
}