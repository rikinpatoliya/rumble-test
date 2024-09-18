package com.rumble.domain.uploadmanager.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.UpdateUploadVideoProgressUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.uploadmanager.UploadManagerConstants
import com.rumble.network.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@HiltWorker
class UserProfileEmailVerificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getUploadVideoUseCase: GetUploadVideoUseCase,
    private val updateUploadVideoProgressUseCase: UpdateUploadVideoProgressUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val uuid = inputData.getString(UploadManagerConstants.KEY_UPLOAD_UUID)
        runBlocking {
            uuid?.let { getUploadVideoUseCase(it) }?.let { uploadVideoData ->
                val result = getUserProfileUseCase()
                if (result.success && result.userProfileEntity?.validated == true) {
                    Result.success()
                } else {
                    updateUploadVideoProgressUseCase(
                        uploadVideoData = uploadVideoData,
                        status = UploadStatus.EMAIL_VERIFICATION_NEEDED,
                        progress = uploadVideoData.progress
                    )
                    Result.failure()
                }
            }
        } ?: Result.failure()
    }
}