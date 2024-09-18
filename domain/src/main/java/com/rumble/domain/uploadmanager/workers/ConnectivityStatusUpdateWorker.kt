package com.rumble.domain.uploadmanager.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.UpdateUploadVideoProgressUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.uploadmanager.UploadManagerConstants
import com.rumble.network.connection.NetworkType
import com.rumble.network.connection.NetworkTypeResolver
import com.rumble.network.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@HiltWorker
class ConnectivityStatusUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val networkTypeResolver: NetworkTypeResolver,
    private val getUploadVideoUseCase: GetUploadVideoUseCase,
    private val updateUploadVideoProgressUseCase: UpdateUploadVideoProgressUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val uuid = inputData.getString(UploadManagerConstants.KEY_UPLOAD_UUID)
        runBlocking {
            uuid?.let { getUploadVideoUseCase(it) }?.let { uploadVideoData ->
                val isOverWifiOnly =
                    runBlocking { userPreferenceManager.uploadOverWifiFLow.first() }
                val networkType = networkTypeResolver.typeOfNetwork()
                if (networkType == NetworkType.NONE) {
                    updateUploadVideoProgressUseCase(
                        uploadVideoData = uploadVideoData,
                        status = UploadStatus.WAITING_CONNECTION,
                        progress = uploadVideoData.progress
                    )
                } else if (isOverWifiOnly && networkType != NetworkType.WI_FI) {
                    updateUploadVideoProgressUseCase(
                        uploadVideoData = uploadVideoData,
                        status = UploadStatus.WAITING_WIFI,
                        progress = uploadVideoData.progress
                    )
                }
                Result.success()
            }
        } ?: Result.failure()
    }
}