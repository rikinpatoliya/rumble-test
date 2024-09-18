package com.rumble.domain.feed.model.datasource.remote

import androidx.paging.PagingState
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.network.api.VideoApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LiveVideoPagingSource(
    private val videoApi: VideoApi,
    private val dispatcher: CoroutineDispatcher,
    private val shortList: Boolean,
) : RumblePagingSource<Int, Feed>() {

    private var nextKey = 0
    private var offset = 0

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
                nextKey = params.key ?: 0
                val items = fetchChannelVideoList(loadSize)
                val filteredItems: List<VideoEntity> =
                    sanitizeDuplicates(items).mapIndexed { index, videoEntity ->
                        videoEntity.copy(index = index)
                    }

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

    private suspend fun fetchChannelVideoList(loadSize: Int): List<VideoEntity> {
        val front = if (shortList) 1 else 0
        val response =
            videoApi.fetchLiveVideoList(offset, loadSize, front).body()
        val items = response?.data?.items?.map { it.getVideoEntity() } ?: emptyList()
        offset += loadSize
        return items
    }

}