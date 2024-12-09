package com.rumble.domain.channels.model.repository

import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelFollow
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelType
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FetchChannelDataResult
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsData
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.queryHelpers.Sort
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {

    suspend fun fetchChannelData(id: String): FetchChannelDataResult

    suspend fun fetchUserUploadChannels(): UserUploadChannelsResult

    suspend fun logChannelView(channelId: String)

    fun fetchChannelVideos(id: String, sortType: Sort, pageSize: Int): Flow<PagingData<Feed>>

    suspend fun updateChannelSubscription(
        id: String,
        type: ChannelType,
        action: UpdateChannelSubscriptionAction,
        data: UpdateChannelNotificationsData? = null,
    ): Result<CreatorEntity>

    suspend fun listOfFollowedChannels(): Result<List<CreatorEntity>>

    fun pagingOfFeaturedChannels(): Flow<PagingData<CreatorEntity>>

    suspend fun listOfFeaturedChannels(): ChannelListResult

    fun fetchFollowedChannels(): Flow<PagingData<CreatorEntity>>

    fun fetchChannelFollowUpdates(): Flow<List<ChannelFollow>>
    suspend fun fetchFollowedChannelsV2(): ChannelListResult
}