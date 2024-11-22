package com.rumble.domain.feed.model.datasource.remote

import androidx.paging.PagingState
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.channel.FeaturedChannelsFeedItem
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.model.getFeed
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.domain.repost.getRepostEntity
import com.rumble.network.api.RepostApi
import com.rumble.network.api.VideoApi
import com.rumble.network.queryHelpers.LiveVideoFront
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class VideoCollectionPagingSource(
    private val videoCollectionType: VideoCollectionType,
    private val videoApi: VideoApi,
    private val repostApi: RepostApi,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, Feed>() {

    private var subscribedOffset = 0
    private val featuredChannelsMaxIndex = 10
    private var nextKey = 0

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
                nextKey = params.key ?: 0

                val subscriptionItems = fetchVideos(
                    videoCollectionType = videoCollectionType,
                    loadSize = getLoadSize(loadSize)
                )

                val filteredItems: List<Feed> = sanitizeDuplicatesById(subscriptionItems)
                val items: MutableList<Feed> = filteredItems.toMutableList()

                if (nextKey == 0) {
                    if (items.size <= 10) {
                        items.add(FeaturedChannelsFeedItem(nextKey))
                    } else {
                        items.add(
                            featuredChannelsMaxIndex,
                            FeaturedChannelsFeedItem(nextKey)
                        )
                    }
                }

                val itemsWithIndex: List<Feed> = items.mapIndexed { index, it ->
                    when (it) {
                        is FeaturedChannelsFeedItem -> it.copy(index = nextKey + index)
                        is VideoEntity -> it.copy(index = nextKey + index)
                        else -> (it as? RepostEntity)?.copy(index = nextKey + index)
                    }
                }.mapNotNull { it }

                LoadResult.Page(
                    data = itemsWithIndex,
                    prevKey = null,
                    nextKey = when {
                        itemsWithIndex.isEmpty() -> null
                        nextKey == 0 -> loadSize + 1 // added 1 to account for featured channels
                        else -> nextKey + loadSize
                    }
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override val keyReuseSupported: Boolean
        get() = true

    private suspend fun fetchVideos(
        videoCollectionType: VideoCollectionType,
        loadSize: Int
    ): List<Feed> {
        return when (videoCollectionType) {
            is VideoCollectionType.VideoCollectionEntity -> {
                if (videoCollectionType.name.trim().lowercase() == "live") {
                    fetchLiveVideoList(loadSize = loadSize)
                } else {
                    fetchVideoCollection(
                        id = videoCollectionType.id,
                        loadSize = loadSize
                    )
                }
            }

            VideoCollectionType.MyFeed -> fetchSubscriptionVideoList(loadSize = loadSize)
            VideoCollectionType.Reposts -> fetchRepostList(loadSize = loadSize)
        }
    }

    private suspend fun fetchLiveVideoList(loadSize: Int): List<Feed> {
        val response =
            videoApi.fetchLiveVideoList(
                offset = subscribedOffset,
                limit = loadSize,
                front = LiveVideoFront.NotFront.value
            )
                .body()
        val items = response?.data?.items?.map { it.getFeed() }?.mapNotNull { it } ?: emptyList()
        subscribedOffset += loadSize
        return items
    }

    private suspend fun fetchVideoCollection(
        id: String,
        loadSize: Int,
    ): List<Feed> {
        val responseBody =
            videoApi.fetchVideoCollection(
                id,
                offset = subscribedOffset,
                limit = loadSize
            ).body()
        val items = responseBody?.data?.items?.map {
            it.getFeed()
        }?.mapNotNull { it } ?: emptyList()
        subscribedOffset += loadSize
        return items
    }

    private suspend fun fetchSubscriptionVideoList(loadSize: Int): List<Feed> {
        val response =
            videoApi.fetchSubscriptionVideoList(offset = subscribedOffset, limit = loadSize).body()
        val items = response?.data?.items?.map { it.getFeed() }?.mapNotNull { it } ?: emptyList()
        subscribedOffset += loadSize
        return items
    }

    private suspend fun fetchRepostList(loadSize: Int): List<Feed> {
        val repostListResponse = repostApi.fetchFeedReposts(offset = nextKey, limit = loadSize)
        return repostListResponse.body()?.data?.items?.map { it.getRepostEntity() } ?: emptyList()
    }
}