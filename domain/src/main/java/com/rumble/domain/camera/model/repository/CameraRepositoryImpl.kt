package com.rumble.domain.camera.model.repository

import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.model.datasource.local.VideoDao
import com.rumble.domain.camera.model.datasource.remote.CameraRemoteDataSource
import com.rumble.domain.uploadmanager.dto.UploadVideoData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class CameraRepositoryImpl(
    private val cameraRemoteDataSource: CameraRemoteDataSource,
    private val videoDao: VideoDao,
    private val dispatcher: CoroutineDispatcher,
) : CameraRepository {

    override suspend fun uploadVideo(uploadVideoData: UploadVideoData, forcedOverCellular: Boolean) =
        withContext(dispatcher) {
            cameraRemoteDataSource.uploadVideo(uploadVideoData, forcedOverCellular)
        }

    override suspend fun saveVideo(uploadVideoData: UploadVideoData) =
        videoDao.saveVideo(uploadVideoData.getRoomVideo())

    override fun getVideosList(): Flow<List<UploadVideoData>> =
        videoDao.fetchVideoList().map {
            it.map { roomVideo ->
                roomVideo.getUploadVideoData()
            }
        }

    override suspend fun getUploadVideoList(status: UploadStatus): List<UploadVideoData> =
        videoDao.fetchVideoListByStatus(status.value).map { it.getUploadVideoData() }

    override suspend fun getUploadVideo(uuid: String): UploadVideoData? {
        return videoDao.fetchByUuid(uuid)?.getUploadVideoData()
    }

    override suspend fun cancelVideoUpload(uuid: String) {
        videoDao.deleteVideo(uuid)
        cameraRemoteDataSource.cancelUploadVideo(uuid)
    }

    override suspend fun deleteVideoUpload(uuid: String) {
        videoDao.deleteVideo(uuid)
    }

    override suspend fun restartVideoUpload(uuid: String, forcedOverCellular: Boolean) {
        videoDao.fetchByUuid(uuid)?.getUploadVideoData()?.let { uploadVideoData ->
            val newUploadUUID = UUID.randomUUID().toString()
            val newUploadVideoData = uploadVideoData.copy(
                uploadUUID = newUploadUUID,
                status = UploadStatus.DRAFT,
                progress = 0F,
                errorMessage = null,
                tempThumbUrl = uploadVideoData.tempThumbUrl,
                tempVideoUrl = null,
                uploadedThumbRef = null,
                uploadedVideoRef = null
            )
            saveVideo(newUploadVideoData)
            videoDao.deleteVideo(uuid)
            uploadVideo(newUploadVideoData, forcedOverCellular)
        }
        cameraRemoteDataSource.cancelUploadVideo(uuid)
    }
}