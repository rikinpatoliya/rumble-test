package com.rumble.domain.channels.model.repository

import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelType
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FetchChannelDataResult
import com.rumble.domain.channels.channeldetails.domain.domainmodel.RoomChannelFollow
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsData
import com.rumble.domain.channels.model.datasource.ChannelRemoteDataSource
import com.rumble.domain.channels.model.datasource.local.ChannelFollowDao
import com.rumble.domain.database.getChannelFollow
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.model.datasource.local.ChannelViewDao
import com.rumble.domain.feed.model.datasource.local.RoomChannelView
import com.rumble.network.queryHelpers.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChannelRepositoryImpl(
    private val channelRemoteDataSource: ChannelRemoteDataSource,
    private val channelViewDao: ChannelViewDao,
    private val channelFollowDao: ChannelFollowDao,
    private val dispatcher: CoroutineDispatcher,
) : ChannelRepository {

    override suspend fun fetchChannelData(id: String): FetchChannelDataResult =
        withContext(dispatcher) {
            channelRemoteDataSource.fetchChannelData(id)
        }

    override suspend fun fetchUserUploadChannels(): UserUploadChannelsResult =
        withContext(dispatcher) {
            channelRemoteDataSource.fetchUserUploadChannels()
        }

    override suspend fun logChannelView(channelId: String) {
        channelViewDao.save(
            roomChannelView = RoomChannelView(
                channelId = channelId,
                time = LocalDateTime.now().toEpochSecond(
                    ZoneOffset.UTC
                )
            )
        )
    }

    override fun fetchChannelVideos(id: String, sortType: Sort, pageSize: Int): Flow<PagingData<Feed>> =
        channelRemoteDataSource.fetchChannelVideos(id, sortType, pageSize)

    override suspend fun updateChannelSubscription(
        id: String,
        type: ChannelType,
        action: UpdateChannelSubscriptionAction,
        data: UpdateChannelNotificationsData?,
    ): Result<CreatorEntity> {
        val result = channelRemoteDataSource.updateChannelSubscription(
            id = id,
            type = type,
            action = action,
            data = data,
        )

        if (result.isSuccess) {
            result.getOrNull()?.let {
                channelFollowDao.save(
                    RoomChannelFollow(
                        channelId = it.channelId,
                        followed = it.followed,
                        time = LocalDateTime.now().toEpochSecond(
                            ZoneOffset.UTC
                        )
                    )
                )
            }
        }

        return result
    }

    override suspend fun listOfFollowedChannels() = channelRemoteDataSource.listOfFollowedChannels()

    override fun pagingOfFeaturedChannels(): Flow<PagingData<CreatorEntity>> =
        channelRemoteDataSource.pagingOfFeaturedChannels()

    override suspend fun listOfFeaturedChannels(): ChannelListResult =
        channelRemoteDataSource.fetchFeaturedChannels()

    override fun fetchFollowedChannels(): Flow<PagingData<CreatorEntity>> =
        channelRemoteDataSource.fetchFollowedChannels()

    override fun fetchChannelFollowUpdates() =
        channelFollowDao.getChannelFollowUpdatesAfterCurrentTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
            .map { list -> list.map { it.getChannelFollow() } }

    override suspend fun fetchFollowedChannelsV2(): ChannelListResult =
        channelRemoteDataSource.fetchFollowedChannelsV2()
}