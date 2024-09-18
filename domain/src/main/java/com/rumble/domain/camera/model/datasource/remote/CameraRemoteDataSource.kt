package com.rumble.domain.camera.model.datasource.remote

import com.rumble.domain.uploadmanager.dto.UploadVideoData

interface CameraRemoteDataSource {

    suspend fun uploadVideo(uploadVideoData: UploadVideoData, forcedOverCellular: Boolean = false)

    suspend fun cancelUploadVideo(uuid: String)
}