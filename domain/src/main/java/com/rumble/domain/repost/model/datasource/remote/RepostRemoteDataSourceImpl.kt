package com.rumble.domain.repost.model.datasource.remote

import androidx.paging.Pager
import androidx.paging.PagingData
import com.rumble.domain.common.model.getRumblePagingConfig
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.api.RepostApi
import com.rumble.network.dto.repost.DeleteRepostResponse
import com.rumble.network.dto.repost.RepostResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class RepostRemoteDataSourceImpl(
    private val repostApi: RepostApi,
    private val dispatcher: CoroutineDispatcher,
) : RepostRemoteDataSource {

    override fun fetchFeedRepostData(pageSize: Int): Flow<PagingData<Feed>> =
        Pager(
            config = getRumblePagingConfig(pageSize = pageSize),
            pagingSourceFactory = {
                FeedRepostPagingSource(
                    repostApi = repostApi,
                    dispatcher = dispatcher,
                )
            }).flow

    override fun fetchRepostData(
        userId: String?,
        channelId: String?,
        pageSize: Int
    ): Flow<PagingData<Feed>> =
        Pager(
            config = getRumblePagingConfig(pageSize = pageSize),
            pagingSourceFactory = {
                RepostPagingSource(
                    userId = userId,
                    channelId = channelId,
                    repostApi = repostApi,
                    dispatcher = dispatcher,
                )
            }).flow

    override suspend fun deleteRepost(repostId: Long): Response<DeleteRepostResponse> =
        repostApi.deleteRepost(repostId)

    override suspend fun addRepost(
        videoId: Long,
        channelId: Long?,
        message: String,
    ): Response<RepostResponse> =
        repostApi.addRepost(videoId, channelId, message)
}