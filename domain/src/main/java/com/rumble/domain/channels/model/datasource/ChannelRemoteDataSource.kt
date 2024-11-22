package com.rumble.domain.channels.model.datasource

import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.*
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsData
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.queryHelpers.Sort
import kotlinx.coroutines.flow.Flow

interface ChannelRemoteDataSource {

    suspend fun fetchChannelData(id: String): Result<CreatorEntity>

    suspend fun fetchUserUploadChannels(): UserUploadChannelsResult

    fun fetchChannelVideos(id: String, sortType: Sort, pageSize: Int): Flow<PagingData<Feed>>

    suspend fun updateChannelSubscription(
        id: String,
        type: ChannelType,
        action: UpdateChannelSubscriptionAction,
        data: UpdateChannelNotificationsData?,
    ): Result<CreatorEntity>

    suspend fun listOfFollowedChannels(): Result<List<CreatorEntity>>

    fun fetchFollowedChannels(): Flow<PagingData<CreatorEntity>>

    fun pagingOfFeaturedChannels(): Flow<PagingData<CreatorEntity>>

    suspend fun fetchFeaturedChannels(
        offset: Int? = null,
        limit: Int? = null,
    ): ChannelListResult

    suspend fun fetchFreshChannels(): ChannelListResult
    suspend fun fetchFollowedChannelsV2(): ChannelListResult
}