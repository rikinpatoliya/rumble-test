package com.rumble.domain.camera.domain.usecases

import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.model.repository.CameraRepository
import com.rumble.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RestartWaitingConnectionVideoUploadsUseCase @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val restartUploadVideoUseCase: RestartUploadVideoUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    suspend operator fun invoke() {
        withContext(ioDispatcher) {
            cameraRepository.getUploadVideoList(UploadStatus.WAITING_CONNECTION).forEach {
                restartUploadVideoUseCase(it.uploadUUID)
            }
        }
    }
}