package com.rumble.domain.uploadmanager.workers

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.core.net.toFile
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rumble.domain.R
import com.rumble.domain.camera.UploadProgress
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.UpdateUploadVideoProgressUseCase
import com.rumble.domain.uploadmanager.UploadManagerConstants
import com.rumble.domain.uploadmanager.UploadManagerConstants.THUMB_UPLOAD_ERROR
import com.rumble.network.api.CameraApi
import com.rumble.network.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import kotlin.random.Random

@HiltWorker
class UploadVideoThumbnailWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val cameraApi: CameraApi,
    private val getUploadVideoUseCase: GetUploadVideoUseCase,
    private val updateUploadVideoProgressUseCase: UpdateUploadVideoProgressUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val uuid = inputData.getString(UploadManagerConstants.KEY_UPLOAD_UUID)
        runBlocking {
            uuid?.let { getUploadVideoUseCase(it) }?.let { uploadVideoData ->
                updateUploadVideoProgressUseCase(
                    uploadVideoData = uploadVideoData,
                    status = UploadStatus.UPLOADING,
                    progress = UploadProgress.UPLOAD_THUMB_STARTED.value
                )
                try {
                    val uriStr = uploadVideoData.tempThumbUrl
                    if (TextUtils.isEmpty(uriStr)) throw IllegalArgumentException("Invalid uri!")
                    val uri = Uri.parse(uriStr)
                    val filename =
                        "ct-${System.currentTimeMillis()}-${Random.nextInt(100000, 200000)}.png"
                    val response = cameraApi.uploadVideoThumbnail(
                        filename = filename,
                        thumbnailImage = MultipartBody.Part
                            .createFormData(
                                name = "customThumb",
                                filename = filename,
                                body = uri.toFile().asRequestBody()
                            ),
                    )
                    val responseBody = response.body()
                    if (response.isSuccessful && responseBody != null) {
                        if (responseBody.thumbnail == THUMB_UPLOAD_ERROR) {
                            updateUploadVideoProgressUseCase(
                                uploadVideoData = uploadVideoData,
                                status = UploadStatus.UPLOADING_FAILED,
                                progress = UploadProgress.UPLOAD_THUMB_ENDED.value,
                                errorMessage = applicationContext.getString(R.string.error_failed_to_upload_thumbnail)
                            )
                            Result.failure()
                        } else {
                            updateUploadVideoProgressUseCase(
                                uploadVideoData = uploadVideoData,
                                status = UploadStatus.UPLOADING,
                                progress = UploadProgress.UPLOAD_THUMB_ENDED.value,
                                uploadedThumbRef = responseBody.thumbnail
                            )
                            Result.success()
                        }
                    } else {
                        updateUploadVideoProgressUseCase(
                            uploadVideoData = uploadVideoData,
                            status = UploadStatus.UPLOADING_FAILED,
                            progress = UploadProgress.UPLOAD_THUMB_ENDED.value
                        )
                        Result.failure()
                    }
                } catch (t: Throwable) {
                    updateUploadVideoProgressUseCase(
                        uploadVideoData = uploadVideoData,
                        status = UploadStatus.UPLOADING_FAILED,
                        progress = UploadProgress.UPLOAD_THUMB_ENDED.value
                    )
                    Result.failure()
                }
            }
        } ?: Result.failure()
    }
}