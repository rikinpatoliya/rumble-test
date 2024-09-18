package com.rumble.domain.uploadmanager.workers

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rumble.domain.camera.UploadProgress
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.domain.usecases.AdjustVideoQualityUseCase
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.TrimVideoUseCase
import com.rumble.domain.camera.domain.usecases.UpdateUploadVideoProgressUseCase
import com.rumble.domain.uploadmanager.UploadManagerConstants
import com.rumble.network.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

@HiltWorker
class WriteUploadFilesToTempFilesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getUploadVideoUseCase: GetUploadVideoUseCase,
    private val updateUploadVideoProgressUseCase: UpdateUploadVideoProgressUseCase,
    private val trimVideoUseCase: TrimVideoUseCase,
    private val adjustVideoQualityUseCase: AdjustVideoQualityUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val uuid = inputData.getString(UploadManagerConstants.KEY_UPLOAD_UUID)
        val directoryName = inputData.getString(UploadManagerConstants.KEY_DIRECTORY_NAME)
        runBlocking {
            uuid?.let { getUploadVideoUseCase(it) }?.let { uploadVideoData ->
                val videoUriStr = uploadVideoData.videoUri
                val videoExtension = uploadVideoData.videoExtension
                try {
                    if (videoUriStr.isBlank()) throw IllegalArgumentException(
                        "Invalid uri!"
                    )
                    updateUploadVideoProgressUseCase(
                        uploadVideoData = uploadVideoData,
                        status = UploadStatus.PROCESSING,
                        progress = UploadProgress.STARTED.value
                    )
                    val tempVideoFileName = "rumble_video_temp_${UUID.randomUUID()}.$videoExtension"
                    val outputDir = File(applicationContext.cacheDir, "$directoryName")
                    if (!outputDir.exists()) {
                        outputDir.mkdirs()
                    }
                    val outputVideoFile = File(outputDir, tempVideoFileName)
                    trimVideoUseCase(
                        videoUriStr,
                        outputVideoFile.absolutePath,
                        uploadVideoData.trimStart ?: 0f,
                        uploadVideoData.trimEnd ?: 0f
                    )
                    val tempVideoQualityFileName =
                        "quality_rumble_video_temp_${UUID.randomUUID()}.$videoExtension"
                    val outputVideoQualityFile = File(outputDir, tempVideoQualityFileName)
                    val success = adjustVideoQualityUseCase(
                        outputVideoFile.absolutePath,
                        outputVideoQualityFile.absolutePath,
                        uploadVideoData.uploadQuality
                    )
                    updateUploadVideoProgressUseCase(
                        uploadVideoData = uploadVideoData.copy(
                            tempVideoUrl = Uri.fromFile(if (success) outputVideoQualityFile else outputVideoFile)
                                .toString()
                        ),
                        status = UploadStatus.PROCESSING,
                        progress = UploadProgress.SAVED_AND_TRIMMED_VIDEO_FILE.value
                    )
                    Result.success()
                } catch (t: Throwable) {
                    updateUploadVideoProgressUseCase(
                        uploadVideoData = uploadVideoData,
                        status = UploadStatus.UPLOADING_FAILED,
                        progress = UploadProgress.SAVED_AND_TRIMMED_VIDEO_FILE.value
                    )
                    Result.failure()
                }
            } ?: Result.failure()
        }
    }
}