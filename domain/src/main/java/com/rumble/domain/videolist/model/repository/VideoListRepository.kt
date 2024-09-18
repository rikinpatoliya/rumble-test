package com.rumble.domain.videolist.model.repository

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.queryHelpers.Sort
import kotlinx.coroutines.flow.Flow

interface VideoListRepository {
    fun fetchVideos(id: String, sortType: Sort? = null, pageSize: Int): Flow<PagingData<Feed>>
    fun fetchBattlesVideos(leaderboard: Boolean, pageSize: Int): Flow<PagingData<Feed>>
}