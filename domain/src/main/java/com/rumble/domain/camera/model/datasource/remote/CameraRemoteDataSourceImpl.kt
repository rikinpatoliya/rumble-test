package com.rumble.domain.camera.model.datasource.remote

import com.rumble.domain.uploadmanager.UploadManager
import com.rumble.domain.uploadmanager.dto.UploadVideoData

class CameraRemoteDataSourceImpl(
    private val uploadManager: UploadManager,
) : CameraRemoteDataSource {

    override suspend fun uploadVideo(uploadVideoData: UploadVideoData, forcedOverCellular: Boolean) = uploadManager.uploadVideo(uploadVideoData, forcedOverCellular)

    override suspend fun cancelUploadVideo(uuid: String) = uploadManager.cancelUploadVideo(uuid)
}