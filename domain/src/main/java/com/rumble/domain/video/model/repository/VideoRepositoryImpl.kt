package com.rumble.domain.video.model.repository

import com.rumble.domain.common.domain.domainmodel.EmptyResult
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.datasource.UserRemoteDataSource
import com.rumble.domain.common.model.datasource.VideoRemoteDataSource
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.domain.video.domain.domainmodel.FetchRelatedVideoListResult
import com.rumble.domain.video.model.datasource.local.LastPositionDao
import com.rumble.domain.video.model.datasource.local.RoomLastPosition
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "VideoRepositoryImpl"

class VideoRepositoryImpl(
    private val lastPositionDao: LastPositionDao,
    private val dispatcher: CoroutineDispatcher,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
) : VideoRepository {

    private val scope = CoroutineScope(dispatcher)

    override suspend fun getLastPosition(userId: String, videoId: Long): Long? =
        withContext(dispatcher) {
            lastPositionDao.getLastPosition(userId, videoId)?.lastPosition
        }

    override fun saveLastPosition(userId: String, videoId: Long, position: Long) {
        scope.launch {
            val roomLastPosition = RoomLastPosition(
                userId = userId,
                videoId = videoId,
                lastPosition = position
            )
            lastPositionDao.updateLastPosition(roomLastPosition)
        }
    }

    override suspend fun requestVerificationEmail(email: String): EmptyResult {
        return userRemoteDataSource.requestVerificationEmail(email)
    }

    override suspend fun fetchRelatedVideoList(videoId: Long): FetchRelatedVideoListResult {
        val response = videoRemoteDataSource.fetchRelatedVideoList(videoId)
        return if (response.isSuccessful)
            FetchRelatedVideoListResult.Success(response.body()?.videoData?.items?.map { it.getVideoEntity() }
                ?: emptyList())
        else FetchRelatedVideoListResult.Failure(RumbleError(TAG, response.raw()))
    }
}