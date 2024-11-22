package com.rumble.domain.channels.model.datasource

import androidx.paging.Pager
import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelType
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsData
import com.rumble.domain.common.model.getRumblePagingConfig
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.model.getCreatorEntity
import com.rumble.domain.feed.model.getUserUploadChannelEntity
import com.rumble.network.api.ChannelApi
import com.rumble.network.api.UserApi
import com.rumble.network.api.VideoApi
import com.rumble.network.queryHelpers.Action
import com.rumble.network.queryHelpers.Sort
import com.rumble.network.queryHelpers.Type
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.FormBody


private const val TAG = "ChannelRemoteDataSourceImpl"

class ChannelRemoteDataSourceImpl(
    private val channelApi: ChannelApi,
    private val videoApi: VideoApi,
    private val userApi: UserApi,
    private val dispatcher: CoroutineDispatcher,
) : ChannelRemoteDataSource {

    override suspend fun fetchChannelData(id: String): Result<CreatorEntity> {
        val response = channelApi.fetchChannelData(id)
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null)
            Result.success(responseBody.data.getCreatorEntity())
        else
            Result.failure(IllegalStateException("fetchChannelData failed"))
    }

    override suspend fun fetchUserUploadChannels(): UserUploadChannelsResult {
        val response = userApi.fetchUploadChannels()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            UserUploadChannelsResult.UserUploadChannelsSuccess(body.data.map {
                it.getUserUploadChannelEntity()
            })
        } else {
            UserUploadChannelsResult.UserUploadChannelsError(RumbleError(TAG, response.raw()))
        }
    }

    override fun fetchChannelVideos(id: String, sortType: Sort, pageSize: Int): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(
                pageSize = pageSize,
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

    override suspend fun updateChannelSubscription(
        id: String,
        type: ChannelType,
        action: UpdateChannelSubscriptionAction,
        data: UpdateChannelNotificationsData?,
    ): Result<CreatorEntity> {
        val response = withContext(dispatcher) {
            channelApi.updateSubscription(
                subscriptionBody = FormBody.Builder()
                    .add("id", id)
                    .add("type", Type.getByValue(type.value).toString())
                    .add("action", Action.getByValue(action.value).toString())
                    .apply {
                        data?.let { updateData ->
                            this.add(
                                "is_push_ls_enabled",
                                updateData.pushNotificationsEnabled.toString()
                            )
                            this.add(
                                "notification",
                                updateData.emailNotificationsEnabled.toString()
                            )
                            updateData.notificationFrequency?.let {
                                this.add("frequency", it.frequency.toString())
                            }
                        }
                    }
                    .build()
            )
        }
        val responseBody = response.body()
        return if (response.isSuccessful && responseBody != null)
            Result.success(responseBody.data.getCreatorEntity())
        else
            Result.failure(IllegalStateException("updateChannelSubscription failed"))
    }

    override suspend fun listOfFollowedChannels(): Result<List<CreatorEntity>> {
        val response = channelApi.listOfFollowedChannels()
        val responseBody = response.body()

        return if (response.isSuccessful && responseBody != null)
            Result.success(responseBody.data.items.map { it.getCreatorEntity() })
        else
            Result.failure(RuntimeException("listOfFollowedChannels failed"))
    }

    override fun fetchFollowedChannels(): Flow<PagingData<CreatorEntity>> {
        return Pager(
            config = getRumblePagingConfig(),
            pagingSourceFactory = {
                FollowedChannelsPagingSource(
                    channelApi = channelApi,
                    dispatcher = dispatcher,
                )
            }).flow
    }

    override fun pagingOfFeaturedChannels(): Flow<PagingData<CreatorEntity>> {
        return Pager(
            config = getRumblePagingConfig(),
            pagingSourceFactory = {
                FeaturedChannelsPagingSource(
                    channelApi = channelApi,
                    dispatcher = dispatcher,
                )
            }).flow
    }

    override suspend fun fetchFeaturedChannels(
        offset: Int?,
        limit: Int?,
    ): ChannelListResult {
        val response = channelApi.fetchFeaturedChannels(offset, limit)
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            ChannelListResult.Success(body.data.items.map { it.getCreatorEntity() })
        } else {
            ChannelListResult.Failure(RumbleError(TAG, response.raw()))
        }
    }

    override suspend fun fetchFreshChannels(): ChannelListResult {
        val response = channelApi.fetchFreshChannels()
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            ChannelListResult.Success(body.data.items.map { it.getCreatorEntity() })
        } else {
            ChannelListResult.Failure(RumbleError(TAG, response.raw()))
        }
    }

    override suspend fun fetchFollowedChannelsV2(): ChannelListResult {
        val response = channelApi.fetchFollowedChannels()
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            ChannelListResult.Success(body.data.items?.map { it.getCreatorEntity() } ?: emptyList())
        } else {
            ChannelListResult.Failure(RumbleError(TAG, response.raw()))
        }
    }
}