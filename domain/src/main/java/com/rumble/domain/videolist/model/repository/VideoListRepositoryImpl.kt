package com.rumble.domain.videolist.model.repository

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.videolist.model.datasource.VideoListRemoteDataSource
import com.rumble.network.queryHelpers.Sort
import kotlinx.coroutines.flow.Flow

class VideoListRepositoryImpl(
    private val videoListRemoteDataSource: VideoListRemoteDataSource,
) : VideoListRepository {
    override fun fetchVideos(id: String, sortType: Sort?, pageSize: Int): Flow<PagingData<Feed>> =
        videoListRemoteDataSource.fetchVideos(id = id, sortType = sortType, pageSize = pageSize)

    override fun fetchBattlesVideos(leaderboard: Boolean, pageSize: Int): Flow<PagingData<Feed>> =
        videoListRemoteDataSource.fetchBattlesVideos(leaderboard = leaderboard, pageSize = pageSize)
}