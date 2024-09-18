package com.rumble.domain.video.model.repository

import com.rumble.domain.common.domain.domainmodel.EmptyResult
import com.rumble.domain.video.domain.domainmodel.FetchRelatedVideoListResult

interface VideoRepository {
    suspend fun getLastPosition(userId: String, videoId: Long): Long?
    fun saveLastPosition(userId: String, videoId: Long, position: Long)
    suspend fun requestVerificationEmail(email: String): EmptyResult
    suspend fun fetchRelatedVideoList(videoId: Long): FetchRelatedVideoListResult
}