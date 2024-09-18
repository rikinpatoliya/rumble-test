package com.rumble.domain.uploadmanager

import android.net.Uri
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.uploadmanager.UploadManagerConstants.IMAGE_DEFAULT_EXTENSION
import com.rumble.domain.uploadmanager.dto.UploadVideoData
import com.rumble.domain.uploadmanager.workers.CleanTempFilesWorker
import com.rumble.domain.uploadmanager.workers.CleanTempFolderWorker
import com.rumble.domain.uploadmanager.workers.ConnectivityStatusUpdateWorker
import com.rumble.domain.uploadmanager.workers.SetVideoMetadataWorker
import com.rumble.domain.uploadmanager.workers.UploadUserImageWorker
import com.rumble.domain.uploadmanager.workers.UploadVideoThumbnailWorker
import com.rumble.domain.uploadmanager.workers.UploadVideoWorker
import com.rumble.domain.uploadmanager.workers.UserProfileEmailVerificationWorker
import com.rumble.domain.uploadmanager.workers.WriteToTempFileWorker
import com.rumble.domain.uploadmanager.workers.WriteUploadFilesToTempFilesWorker
import com.rumble.domain.video.domain.usecases.CreateTempDirectoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

const val RUMBLE_VIDEO_UPLOAD_TAG = "RumbleVideoUploadTag"

class UploadManager @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val createTempDirectoryUseCase: CreateTempDirectoryUseCase,
    private val workManager: WorkManager
) {

    fun uploadUserImage(imageUri: Uri) {
        val cleanTempFileWorker = OneTimeWorkRequestBuilder<CleanTempFilesWorker>()
            .setInputData(
                Data.Builder()
                    .putString(
                        UploadManagerConstants.KEY_DIRECTORY_NAME,
                        UploadManagerConstants.TEMP_DIRECTORY
                    )
                    .build()
            )
            .build()

        var continuation =
            workManager.beginWith(cleanTempFileWorker)

        val writeToTempFileWorker =
            OneTimeWorkRequestBuilder<WriteToTempFileWorker>().setInputData(
                Data.Builder()
                    .putString(
                        UploadManagerConstants.KEY_DIRECTORY_NAME,
                        UploadManagerConstants.TEMP_DIRECTORY
                    )
                    .putString(UploadManagerConstants.KEY_URI, imageUri.toString())
                    .putString(UploadManagerConstants.KEY_EXTENSION, IMAGE_DEFAULT_EXTENSION)
                    .build()
            ).build()
        continuation = continuation.then(writeToTempFileWorker)

        val uploadUserImageWorker = OneTimeWorkRequestBuilder<UploadUserImageWorker>().build()
        continuation = continuation.then(uploadUserImageWorker)

        continuation.enqueue()
    }

    fun cancelUploadVideo(uuid: String) {
        workManager.cancelAllWorkByTag(uuid)
    }

    suspend fun uploadVideo(uploadVideoData: UploadVideoData, forcedOverCellular: Boolean) {
        withContext(Dispatchers.IO) {
            val isOverWifiOnly = runBlocking { userPreferenceManager.uploadOverWifiFLow.first() }
            val wifiConstraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()
            val connectedConstraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val outputDirectoryName = createTempDirectoryUseCase(uploadVideoData.uploadUUID)

            val connectivityStatusUpdateWorker =
                OneTimeWorkRequestBuilder<ConnectivityStatusUpdateWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(
                                UploadManagerConstants.KEY_UPLOAD_UUID,
                                uploadVideoData.uploadUUID
                            )
                            .build()
                    )
                    .addTag(RUMBLE_VIDEO_UPLOAD_TAG)
                    .addTag(uploadVideoData.uploadUUID)
                    .build()
            var continuation =
                workManager.beginWith(connectivityStatusUpdateWorker)

            val userProfileEmailVerificationWorker =
                OneTimeWorkRequestBuilder<UserProfileEmailVerificationWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(
                                UploadManagerConstants.KEY_UPLOAD_UUID,
                                uploadVideoData.uploadUUID
                            )
                            .build()
                    )
                    .addTag(RUMBLE_VIDEO_UPLOAD_TAG)
                    .addTag(uploadVideoData.uploadUUID)
                    .setConstraints(connectedConstraint)
                    .also {
                        if (forcedOverCellular.not() && isOverWifiOnly) it.setConstraints(
                            wifiConstraints
                        )
                    }
                    .build()
            continuation = continuation.then(userProfileEmailVerificationWorker)

            val writeUploadFilesToTempFilesWorker =
                OneTimeWorkRequestBuilder<WriteUploadFilesToTempFilesWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(
                                UploadManagerConstants.KEY_DIRECTORY_NAME,
                                outputDirectoryName
                            )
                            .putString(
                                UploadManagerConstants.KEY_UPLOAD_UUID,
                                uploadVideoData.uploadUUID
                            )
                            .build()
                    )
                    .addTag(RUMBLE_VIDEO_UPLOAD_TAG)
                    .addTag(uploadVideoData.uploadUUID)
                    .setConstraints(connectedConstraint)
                    .also {
                        if (forcedOverCellular.not() && isOverWifiOnly) it.setConstraints(
                            wifiConstraints
                        )
                    }
                    .build()
            continuation = continuation.then(writeUploadFilesToTempFilesWorker)

            val uploadVideoThumbnailWorker =
                OneTimeWorkRequestBuilder<UploadVideoThumbnailWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(
                                UploadManagerConstants.KEY_UPLOAD_UUID,
                                uploadVideoData.uploadUUID
                            )
                            .build()
                    )
                    .addTag(RUMBLE_VIDEO_UPLOAD_TAG)
                    .addTag(uploadVideoData.uploadUUID)
                    .setConstraints(connectedConstraint)
                    .also {
                        if (forcedOverCellular.not() && isOverWifiOnly) it.setConstraints(
                            wifiConstraints
                        )
                    }
                    .build()
            continuation = continuation.then(uploadVideoThumbnailWorker)

            val uploadVideoWorker =
                OneTimeWorkRequestBuilder<UploadVideoWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(
                                UploadManagerConstants.KEY_FILENAME,
                                "${System.currentTimeMillis() * 1000}-${
                                    Random.nextInt(
                                        100000,
                                        200000
                                    )
                                }"
                            )
                            .putString(
                                UploadManagerConstants.KEY_UPLOAD_UUID,
                                uploadVideoData.uploadUUID
                            )
                            .build()
                    )
                    .addTag(RUMBLE_VIDEO_UPLOAD_TAG)
                    .addTag(uploadVideoData.uploadUUID)
                    .setConstraints(connectedConstraint)
                    .also {
                        if (forcedOverCellular.not() && isOverWifiOnly) it.setConstraints(
                            wifiConstraints
                        )
                    }
                    .build()
            continuation = continuation.then(uploadVideoWorker)

            val setVideoMetadataWorker =
                OneTimeWorkRequestBuilder<SetVideoMetadataWorker>()
                    .setInputData(
                        Data.Builder()
                            .putString(
                                UploadManagerConstants.KEY_UPLOAD_UUID,
                                uploadVideoData.uploadUUID
                            )
                            .build()
                    )
                    .addTag(RUMBLE_VIDEO_UPLOAD_TAG)
                    .addTag(uploadVideoData.uploadUUID)
                    .setConstraints(connectedConstraint)
                    .also {
                        if (forcedOverCellular.not() && isOverWifiOnly) it.setConstraints(
                            wifiConstraints
                        )
                    }
                    .build()
            continuation = continuation.then(setVideoMetadataWorker)

            //Clean up folder
            val cleanUpTempFileWorker = OneTimeWorkRequestBuilder<CleanTempFilesWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(
                            UploadManagerConstants.KEY_UPLOAD_UUID,
                            uploadVideoData.uploadUUID
                        )
                        .putString(UploadManagerConstants.KEY_DIRECTORY_NAME, outputDirectoryName)
                        .build()
                )
                .addTag(RUMBLE_VIDEO_UPLOAD_TAG)
                .addTag(uploadVideoData.uploadUUID)
                .build()
            continuation = continuation.then(cleanUpTempFileWorker)

            val cleanTempFolderWorker = OneTimeWorkRequestBuilder<CleanTempFolderWorker>()
                .setInputData(
                    Data.Builder()
                        .putString(
                            UploadManagerConstants.KEY_UPLOAD_UUID,
                            uploadVideoData.uploadUUID
                        )
                        .putString(UploadManagerConstants.KEY_DIRECTORY_NAME, outputDirectoryName)
                        .build()
                )
                .addTag(RUMBLE_VIDEO_UPLOAD_TAG)
                .addTag(uploadVideoData.uploadUUID)
                .build()
            continuation = continuation.then(cleanTempFolderWorker)

            continuation.enqueue()
        }
    }
}
