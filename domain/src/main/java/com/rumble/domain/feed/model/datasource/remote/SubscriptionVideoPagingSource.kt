package com.rumble.domain.feed.model.datasource.remote

import androidx.paging.PagingState
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.network.api.VideoApi
import com.rumble.network.dto.video.Video
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SubscriptionVideoPagingSource(
    private val videoApi: VideoApi,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, Feed>() {

    private var subscribedOffset = 0
    private var nextKey = 0

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
                nextKey = params.key ?: 0
                val items = fetchSubscriptionVideoList(loadSize)
                val filteredItems: List<VideoEntity> = sanitizeDuplicates(items)

                LoadResult.Page(
                    data = filteredItems,
                    prevKey = null,
                    nextKey = if (filteredItems.isEmpty()) null else nextKey + loadSize
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override val keyReuseSupported: Boolean
        get() = true

    private suspend fun fetchSubscriptionVideoList(loadSize: Int): List<VideoEntity> {
        val response =
            videoApi.fetchSubscriptionVideoList(offset = subscribedOffset, limit = loadSize).body()
        val items = response?.data?.items?.filterIsInstance<Video>()?.map { it.getVideoEntity() } ?: emptyList()
        subscribedOffset += loadSize
        return items
    }

}