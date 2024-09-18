package com.rumble.domain.library.model.datasource

import androidx.paging.PagingState
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.model.getPlayListEntity
import com.rumble.network.api.VideoApi
import com.rumble.network.queryHelpers.PlayListInclude
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlayListPagingSource(
    private val videoApi: VideoApi,
    private val dispatcher: CoroutineDispatcher,
    private val videoIds: List<Long>? = null
) : RumblePagingSource<Int, PlayListEntity>() {

    private var nextKey = 0
    private var offset = 0

    override fun getRefreshKey(state: PagingState<Int, PlayListEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlayListEntity> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
                nextKey = params.key ?: 0
                val items = fetchPage(loadSize)

                LoadResult.Page(
                    data = items,
                    prevKey = null,
                    nextKey = if (items.isEmpty()) null else nextKey + loadSize
                )
            } catch (e: Exception) {
                Timber.e(t = e, "Error: ")
                LoadResult.Error(e)
            }
        }

    override val keyReuseSupported: Boolean
        get() = true

    private suspend fun fetchPage(loadSize: Int): List<PlayListEntity> {
        val response =
            videoApi.fetchPlayLists(
                offset = offset,
                limit = loadSize,
                include = PlayListInclude.All,
                extraHasVideosIds = videoIds
            ).body()
        val items = response?.playListsData?.playLists?.map {
            it.getPlayListEntity()
        } ?: emptyList()
        offset += loadSize
        return items
    }

}