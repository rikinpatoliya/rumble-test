package com.rumble.domain.repost.model.repository

import androidx.paging.PagingData
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.repost.domain.domainmodel.AddRepostResult
import com.rumble.domain.repost.domain.domainmodel.DeleteRepostResult
import com.rumble.domain.repost.model.datasource.remote.RepostRemoteDataSource
import com.rumble.network.dto.livechat.ErrorResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Converter

private const val TAG = "RepostRepositoryImpl"

class RepostRepositoryImpl(
    private val remoteDataSource: RepostRemoteDataSource,
    private val errorConverter: Converter<ResponseBody, ErrorResponse>?,
    private val dispatcher: CoroutineDispatcher,
) : RepostRepository {

    override fun fetchFeedRepostData(pageSize: Int): Flow<PagingData<Feed>> =
        remoteDataSource.fetchFeedRepostData(pageSize)

    override fun fetchRepostData(
        userId: String?,
        channelId: String?,
        pageSize: Int
    ): Flow<PagingData<Feed>> = remoteDataSource.fetchRepostData(userId, channelId, pageSize)

    override suspend fun deleteRepost(repostId: Long): DeleteRepostResult =
        withContext(dispatcher) {
            val response = remoteDataSource.deleteRepost(repostId)
            if (response.isSuccessful && response.body()?.data?.success == true) {
                DeleteRepostResult.Success
            } else {
                DeleteRepostResult.Failure(
                    rumbleError = RumbleError(
                        tag = TAG,
                        response = response.raw()
                    ),
                    errorMessage = response.errorBody()?.let {
                        val error = errorConverter?.convert(it)
                        error?.errors?.firstOrNull()?.message ?: ""
                    } ?: "",
                )
            }
        }

    override suspend fun addRepost(
        videoId: Long,
        channelId: Long?,
        message: String
    ): AddRepostResult =
        withContext(dispatcher) {
            val response = remoteDataSource.addRepost(videoId, channelId, message)
            if (response.isSuccessful && response.body() != null) {
                AddRepostResult.Success
            } else {
                AddRepostResult.Failure(
                    rumbleError = RumbleError(
                        tag = TAG,
                        response = response.raw()
                    ),
                    errorMessage = response.errorBody()?.let {
                        val error = errorConverter?.convert(it)
                        error?.errors?.firstOrNull()?.message ?: ""
                    } ?: "",
                )
            }
        }
}