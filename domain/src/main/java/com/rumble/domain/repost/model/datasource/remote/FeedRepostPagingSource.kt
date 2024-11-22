package com.rumble.domain.repost.model.datasource.remote

import androidx.paging.PagingState
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.domain.repost.getRepostEntity
import com.rumble.network.api.RepostApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class FeedRepostPagingSource(
    private val repostApi: RepostApi,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, Feed>() {

    private var nextKey = 0

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> =
        withContext(dispatcher) {
            try {
                nextKey = params.key ?: 0
                val loadSize = getLoadSize(params.loadSize)

                val repostListResponse = repostApi.fetchFeedReposts(offset = nextKey, limit = loadSize)
                val repostList = repostListResponse.body()?.data?.items?.map { it.getRepostEntity() } ?: emptyList()

                val itemsWithIndex: List<RepostEntity> = repostList.mapIndexed { index, it ->
                    it.copy(index = nextKey + index)
                }

                LoadResult.Page(
                    data = itemsWithIndex,
                    prevKey = null,
                    nextKey = if (itemsWithIndex.isEmpty()) null else nextKey + loadSize
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override val keyReuseSupported: Boolean
        get() = true
}