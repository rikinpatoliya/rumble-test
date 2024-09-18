package com.rumble.domain.discover.model.repository

import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.discover.domain.domainmodel.CategoryListResult
import com.rumble.domain.discover.domain.domainmodel.CategoryResult
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.queryHelpers.CategoryVideoType
import kotlinx.coroutines.flow.Flow

interface DiscoverRepository {
    suspend fun getLiveNowShortList(): VideoListResult
    suspend fun getEditorsPicks(offset: Int, limit: Int): VideoListResult
    suspend fun getFeaturedChannels(
        offset: Int,
        limit: Int
    ): ChannelListResult

    suspend fun getBattlesLeaderBoardVideos(
        offset: Int,
        limit: Int,
    ): VideoListResult

    suspend fun getCategoryList(limit: Int?): CategoryListResult
    suspend fun getCategory(categoryName: String): CategoryResult
    fun fetchCategoryVideoList(
        categoryName: String,
        videoType: CategoryVideoType,
        pageSize: Int,
    ): Flow<PagingData<Feed>>

    fun fetchCategoryLiveVideoList(pageSize: Int): Flow<PagingData<Feed>>
}