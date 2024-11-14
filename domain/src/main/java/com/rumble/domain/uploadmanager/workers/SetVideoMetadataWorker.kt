package com.rumble.domain.uploadmanager.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rumble.domain.camera.UploadProgress
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.UpdateUploadVideoProgressUseCase
import com.rumble.domain.uploadmanager.UploadManagerConstants
import com.rumble.network.api.CameraApi
import com.rumble.network.di.AppVersion
import com.rumble.network.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import java.time.Instant

@HiltWorker
class SetVideoMetadataWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    @AppVersion private val appVersion: String,
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
                    status = UploadStatus.FINALIZING,
                    progress = UploadProgress.SET_VIDEO_METADATA_STARTED.value
                )
                val response = cameraApi.setVideoMetadata(
                    body = FormBody.Builder()
                        .add("title", uploadVideoData.title)
                        .add("description", uploadVideoData.description)
                        .add("tags", uploadVideoData.tags)
                        .add("featured", "${uploadVideoData.licence}")
                        .also { builder ->
                            uploadVideoData.infoWho?.let {
                                builder.add(
                                    "infoWho",
                                    it
                                )
                            }
                        }
                        .also { builder ->
                            uploadVideoData.infoWhere?.let {
                                builder.add(
                                    "infoWhere",
                                    it
                                )
                            }
                        }
                        .also { builder ->
                            uploadVideoData.infoExtUser?.let {
                                builder.add(
                                    "infoExtUser",
                                    it
                                )
                            }
                        }
                        .add("thumb", "${uploadVideoData.uploadedThumbRef}")
                        .add("rights", "1")
                        .add("terms", "1")
                        .add("video[]", "${uploadVideoData.uploadedVideoRef}")
                        .add("channel", "rumbleStorage")
                        .add("mobileUpload", "1")
                        .add("app[0]", "Rumble")
                        .add("app[1]", appVersion)
                        .add("channelId", "${uploadVideoData.channelId}")
                        .add("siteChannelId", "${uploadVideoData.siteChannelId}")
                        .also { builder ->
                            uploadVideoData.mediaChannelId?.let {
                                builder.add(
                                    "mediaChannelId",
                                    "$it"
                                )
                            }
                        }
                        .also { builder ->
                            if (uploadVideoData.publishDate != null) {
                                builder.add("visibility", "private")
                            } else {
                                builder.add("visibility", uploadVideoData.visibility)
                            }
                        }
                        .also { builder ->
                            uploadVideoData.publishDate?.let {
                                builder.add(
                                    "schedulerDatetime",
                                    Instant.ofEpochMilli(it).toString()
                                )
                            }
                        }
                        .build()
                )
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    if (responseBody.success != 1 || responseBody.data?.fid == null) {
                        updateUploadVideoProgressUseCase(
                            uploadVideoData = uploadVideoData,
                            status = UploadStatus.UPLOADING_FAILED,
                            progress = UploadProgress.SET_VIDEO_METADATA_ENDED.value,
                            errorMessage = responseBody.error?.getFirstError()
                        )
                        Result.failure()
                    } else {
                        updateUploadVideoProgressUseCase(
                            uploadVideoData = uploadVideoData,
                            status = UploadStatus.UPLOADING_SUCCEEDED,
                            progress = UploadProgress.ENDED.value
                        )
                        Result.success()
                    }
                } else {
                    updateUploadVideoProgressUseCase(
                        uploadVideoData = uploadVideoData,
                        status = UploadStatus.UPLOADING_FAILED,
                        progress = UploadProgress.SET_VIDEO_METADATA_ENDED.value
                    )
                    Result.failure()
                }
            } ?: Result.failure()
        }
    }
}