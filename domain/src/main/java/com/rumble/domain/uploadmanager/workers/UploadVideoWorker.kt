package com.rumble.domain.uploadmanager.workers

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.core.net.toFile
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rumble.domain.camera.UploadProgress
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.RestartUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.UpdateUploadVideoProgressUseCase
import com.rumble.domain.uploadmanager.UploadManagerConstants
import com.rumble.domain.uploadmanager.UploadManagerConstants.FILE_GONE_ERROR
import com.rumble.domain.uploadmanager.UploadManagerConstants.VIDEO_UPLOAD_CHUNK_SIZE
import com.rumble.domain.uploadmanager.dto.UploadVideoData
import com.rumble.network.api.CameraApi
import com.rumble.network.di.IoDispatcher
import com.rumble.network.dto.camera.UploadVideoChunkResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.BufferedInputStream
import kotlin.math.min


@HiltWorker
class UploadVideoWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val cameraApi: CameraApi,
    private val getUploadVideoUseCase: GetUploadVideoUseCase,
    private val updateUploadVideoProgressUseCase: UpdateUploadVideoProgressUseCase,
    private val restartUploadVideoUseCase: RestartUploadVideoUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val uuid = inputData.getString(UploadManagerConstants.KEY_UPLOAD_UUID)
        runBlocking {
            uuid?.let { getUploadVideoUseCase(it) }?.let { uploadVideoData ->
                updateUploadVideoProgressUseCase(
                    uploadVideoData = uploadVideoData,
                    status = UploadStatus.UPLOADING,
                    progress = UploadProgress.UPLOAD_VIDEO_STARTED.value
                )
                val uriStr = uploadVideoData.tempVideoUrl
                val bufferSize = VIDEO_UPLOAD_CHUNK_SIZE
                val filename = inputData.getString(UploadManagerConstants.KEY_FILENAME)
                try {
                    if (TextUtils.isEmpty(uriStr)) throw IllegalArgumentException("Invalid uri!")
                    val file = Uri.parse(uriStr).toFile()
                    val totalFileSize = file.length()
                    val chunkQty =
                        (totalFileSize / bufferSize + if (totalFileSize % bufferSize != 0L) 1 else 0).toInt()
                    val chunk = "${filename}.${uploadVideoData.videoExtension}"
                    val totalUploadPercent =
                        UploadProgress.UPLOAD_VIDEO_ENDED.value - UploadProgress.UPLOAD_VIDEO_STARTED.value
                    var success = true

                    //uploading video chunks
                    file.inputStream().use { inputStream ->
                        BufferedInputStream(inputStream).use {

                            var index = 0

                            while (success && inputStream.available() > 0) {
                                val buffer = ByteArray(min(inputStream.available(), bufferSize))
                                inputStream.read(buffer, 0, buffer.size)
                                val response = cameraApi.uploadVideoChunk(
                                    chunkSize = "$bufferSize",
                                    chunkQty = "$chunkQty",
                                    chunk = "${index}_$chunk",
                                    videoChunk = buffer.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                                )
                                index++
                                val currentProgress =
                                    (totalUploadPercent / chunkQty) * index + UploadProgress.UPLOAD_VIDEO_STARTED.value

                                val responseBody = response.body()
                                success =
                                    handleResponse(response, responseBody, uploadVideoData, currentProgress)
                            }
                        }
                    }
                    if (success) {
                        mergeVideoChunks(uploadVideoData, bufferSize, chunkQty, chunk)
                    } else {
                        Result.failure()
                    }
                } catch (t: Throwable) {
                    updateUploadVideoProgressUseCase(
                        uploadVideoData = uploadVideoData,
                        status = UploadStatus.UPLOADING_FAILED,
                        progress = UploadProgress.UPLOAD_VIDEO_ENDED.value
                    )
                    Result.failure()
                }
            } ?: Result.failure()
        }
    }

    private suspend fun handleResponse(
        response: Response<UploadVideoChunkResponse>,
        responseBody: UploadVideoChunkResponse?,
        uploadVideoData: UploadVideoData,
        currentProgress: Float
    ) = if (response.isSuccessful && responseBody != null) {
        if (responseBody.success == 1) {
            updateUploadVideoProgressUseCase(
                uploadVideoData = uploadVideoData,
                status = UploadStatus.UPLOADING,
                progress = currentProgress
            )
            true
        } else if (responseBody.success == 0 && responseBody.code == FILE_GONE_ERROR) {
            restartUploadVideoUseCase(uploadVideoData.uploadUUID)
            false
        } else {
            updateUploadVideoProgressUseCase(
                uploadVideoData = uploadVideoData,
                status = UploadStatus.UPLOADING_FAILED,
                progress = currentProgress,
                errorMessage = responseBody.data
            )
            false
        }
    } else {
        updateUploadVideoProgressUseCase(
            uploadVideoData = uploadVideoData,
            status = UploadStatus.UPLOADING_FAILED,
            progress = currentProgress
        )
        false
    }

    private suspend fun mergeVideoChunks(
        uploadVideoData: UploadVideoData,
        bufferSize: Int,
        chunkQty: Int,
        chunk: String,
    ): Result {
        updateUploadVideoProgressUseCase(
            uploadVideoData = uploadVideoData,
            status = UploadStatus.FINALIZING,
            progress = UploadProgress.MERGE_VIDEO_STARTED.value
        )
        val response = cameraApi.mergeVideoChunks(
            chunkSize = "$bufferSize",
            chunkQty = "$chunkQty",
            chunk = chunk,
            merge = "$chunkQty",
        )
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null) {
            if (responseBody.success == 1) {
                updateUploadVideoProgressUseCase(
                    uploadVideoData = uploadVideoData,
                    status = UploadStatus.FINALIZING,
                    progress = UploadProgress.MERGE_VIDEO_ENDED.value,
                    uploadedVideoRef = responseBody.code
                )
                Result.success()
            } else {
                updateUploadVideoProgressUseCase(
                    uploadVideoData = uploadVideoData,
                    status = UploadStatus.UPLOADING_FAILED,
                    progress = UploadProgress.MERGE_VIDEO_ENDED.value,
                    errorMessage = responseBody.code
                )
                Result.failure()
            }
        } else {
            updateUploadVideoProgressUseCase(
                uploadVideoData = uploadVideoData,
                status = UploadStatus.UPLOADING_FAILED,
                progress = UploadProgress.MERGE_VIDEO_ENDED.value
            )
            Result.failure()
        }
    }
}