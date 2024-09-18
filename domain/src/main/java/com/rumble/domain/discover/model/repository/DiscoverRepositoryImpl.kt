package com.rumble.domain.discover.model.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.model.datasource.ChannelRemoteDataSource
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.common.model.getRumblePagingConfig
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.datasource.VideoRemoteDataSource
import com.rumble.domain.discover.domain.domainmodel.CategoryListResult
import com.rumble.domain.discover.domain.domainmodel.CategoryResult
import com.rumble.domain.discover.model.datasource.CategoryDataSource
import com.rumble.domain.discover.model.datasource.CategoryLiveVideosPagingSource
import com.rumble.domain.discover.model.datasource.CategoryVideosPagingSource
import com.rumble.domain.discover.model.getCategoryEntity
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.api.DiscoverApi
import com.rumble.network.api.VideoApi
import com.rumble.network.queryHelpers.BattlesType
import com.rumble.network.queryHelpers.CategoryVideoType
import com.rumble.network.queryHelpers.LiveVideoFront
import com.rumble.network.queryHelpers.Sort
import com.rumble.network.queryHelpers.VideoCollectionId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

private const val TAG = "DiscoverRepository"

class DiscoverRepositoryImpl(
    private val channelRemoteDataSource: ChannelRemoteDataSource,
    private val videoRemoteDataSource: VideoRemoteDataSource,
    private val categoryDataSource: CategoryDataSource,
    private val dispatcher: CoroutineDispatcher,
    private val discoverApi: DiscoverApi,
    private val videoApi: VideoApi,
) : DiscoverRepository {

    override suspend fun getLiveNowShortList(): VideoListResult = withContext(dispatcher) {
        videoRemoteDataSource.fetchLiveVideos(front = LiveVideoFront.Front)
    }

    override suspend fun getEditorsPicks(
        offset: Int,
        limit: Int
    ): VideoListResult = withContext(dispatcher) {
        videoRemoteDataSource.fetchVideoCollection(
            VideoCollectionId.EditorPicks,
            Sort.DATE,
            offset = offset,
            limit = limit
        )
    }

    override suspend fun getFeaturedChannels(
        offset: Int,
        limit: Int
    ): ChannelListResult = withContext(dispatcher) {
        channelRemoteDataSource.fetchFeaturedChannels(offset = offset, limit = limit)
    }

    override suspend fun getBattlesLeaderBoardVideos(
        offset: Int,
        limit: Int
    ): VideoListResult = withContext(dispatcher) {
        videoRemoteDataSource.fetchBattlesVideos(
            BattlesType.LEADERBOARD,
            offset = offset,
            limit = limit
        )
    }

    override suspend fun getCategoryList(limit: Int?): CategoryListResult =
        withContext(dispatcher) {
            val result = categoryDataSource.fetchCategoryList(limit)
            if (result.isSuccessful and (result.body() != null)) {
                CategoryListResult.Success(result.body()?.data?.items?.map { it.getCategoryEntity() }
                    ?: emptyList())
            } else CategoryListResult.Failure(RumbleError(TAG, result.raw()))
        }

    override suspend fun getCategory(categoryName: String): CategoryResult =
        withContext(dispatcher) {
            val result = categoryDataSource.fetchCategory(categoryName)
            result.body()?.data?.let {
                CategoryResult.Success(
                    category = it.category.getCategoryEntity(),
                    subcategoryList = it.items.map { subcategory -> subcategory.getCategoryEntity() }
                )
            } ?: run {
                CategoryResult.Failure(RumbleError(TAG, result.raw()))
            }
        }

    override fun fetchCategoryVideoList(
        categoryName: String,
        videoType: CategoryVideoType,
        pageSize: Int,
    ): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(
                pageSize = pageSize
            ),
            pagingSourceFactory = {
                CategoryVideosPagingSource(
                    discoverApi = discoverApi,
                    categoryName = categoryName,
                    videoType = videoType,
                    dispatcher = dispatcher
                )
            }).flow
    }

    override fun fetchCategoryLiveVideoList(pageSize: Int): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(
                pageSize = pageSize
            ),
            pagingSourceFactory = {
                CategoryLiveVideosPagingSource(
                    videoApi = videoApi,
                    dispatcher = dispatcher
                )
            }).flow
    }
}