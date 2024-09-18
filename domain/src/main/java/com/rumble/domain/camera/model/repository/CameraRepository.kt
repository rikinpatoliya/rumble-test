package com.rumble.domain.camera.model.repository

import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.uploadmanager.dto.UploadVideoData
import kotlinx.coroutines.flow.Flow


interface CameraRepository {

    suspend fun uploadVideo(uploadVideoData: UploadVideoData, forcedOverCellular: Boolean = false)

    suspend fun saveVideo(uploadVideoData: UploadVideoData)

    fun getVideosList() : Flow<List<UploadVideoData>>

    suspend fun getUploadVideoList(status: UploadStatus) : List<UploadVideoData>

    suspend fun getUploadVideo(uuid: String) : UploadVideoData?

    suspend fun cancelVideoUpload(uuid: String)

    suspend fun deleteVideoUpload(uuid: String)

    suspend fun restartVideoUpload(uuid: String, forcedOverCellular: Boolean)
}