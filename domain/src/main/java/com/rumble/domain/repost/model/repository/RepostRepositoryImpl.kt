package com.rumble.domain.repost.model.repository

import androidx.paging.PagingData
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.repost.domain.domainmodel.AddRepostResult
import com.rumble.domain.repost.domain.domainmodel.DeleteRepostResult
import com.rumble.domain.repost.model.datasource.remote.RepostRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

private const val TAG = "RepostRepositoryImpl"

class RepostRepositoryImpl(
    private val remoteDataSource: RepostRemoteDataSource,
    private val dispatcher: CoroutineDispatcher,
) : RepostRepository {

    override fun fetchFeedRepostData(pageSize: Int): Flow<PagingData<Feed>> =
        remoteDataSource.fetchFeedRepostData(pageSize)

    override fun fetchRepostData(
        userId: String,
        channelId: String,
        pageSize: Int
    ): Flow<PagingData<Feed>> = remoteDataSource.fetchRepostData(userId, channelId, pageSize)

    override suspend fun deleteRepost(repostId: Long): DeleteRepostResult =
        withContext(dispatcher) {
            val response = remoteDataSource.deleteRepost(repostId)
            if (response.isSuccessful && response.body()?.data?.success == true) DeleteRepostResult.Success
            else DeleteRepostResult.Failure(RumbleError(TAG, response.raw()))
        }

    override suspend fun addRepost(
        videoId: Long,
        channelId: Long?,
        message: String
    ): AddRepostResult =
        withContext(dispatcher) {
            val response = remoteDataSource.addRepost(videoId, channelId, message)
            if (response.isSuccessful && response.body() != null) AddRepostResult.Success
            else AddRepostResult.Failure(RumbleError(TAG, response.raw()))
        }
}