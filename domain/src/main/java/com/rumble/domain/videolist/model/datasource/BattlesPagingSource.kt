package com.rumble.domain.videolist.model.datasource

import androidx.paging.PagingState
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.network.api.VideoApi
import com.rumble.network.dto.video.Video
import com.rumble.network.queryHelpers.BattlesType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class BattlesPagingSource(
    private val leaderboard: Boolean,
    private val videoApi: VideoApi,
    private val dispatcher: CoroutineDispatcher
) : RumblePagingSource<Int, Feed>() {

    private var nextKey = 0
    private var offset = 0

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
                nextKey = params.key ?: 0
                val items = fetchPage(loadSize)
                val filteredItems: List<VideoEntity> = sanitizeDuplicates(items)
                    .mapIndexed { index, videoEntity ->
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

    private suspend fun fetchPage(loadSize: Int): List<VideoEntity> {
        val response =
            videoApi.fetchBattlesVideos(
                battlesType = if (leaderboard) BattlesType.LEADERBOARD else null,
                offset = offset,
                limit = loadSize
            ).body()
        val items = response?.data?.items?.filterIsInstance<Video>()?.map { it.getVideoEntity() } ?: emptyList()
        offset += loadSize
        return items
    }

}