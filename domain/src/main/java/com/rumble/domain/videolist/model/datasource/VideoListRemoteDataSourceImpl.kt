package com.rumble.domain.videolist.model.datasource

import androidx.paging.Pager
import androidx.paging.PagingData
import com.rumble.domain.channels.model.datasource.VideoPagingSource
import com.rumble.domain.common.model.getRumblePagingConfig
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.api.VideoApi
import com.rumble.network.queryHelpers.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

class VideoListRemoteDataSourceImpl(
    private val videoApi: VideoApi,
    private val dispatcher: CoroutineDispatcher,
) : VideoListRemoteDataSource {
    
    override fun fetchVideos(id: String, sortType: Sort?, pageSize: Int): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(
                pageSize = pageSize
            ),
            pagingSourceFactory = {
                VideoPagingSource(
                    id = id,
                    videoApi = videoApi,
                    dispatcher = dispatcher,
                    sortType = sortType
                )
            }).flow
    }

    override fun fetchBattlesVideos(leaderboard: Boolean, pageSize: Int): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(
                pageSize = pageSize,
            ),
            pagingSourceFactory = {
                BattlesPagingSource(
                    leaderboard = leaderboard,
                    videoApi = videoApi,
                    dispatcher = dispatcher,
                )
            }).flow
    }
}