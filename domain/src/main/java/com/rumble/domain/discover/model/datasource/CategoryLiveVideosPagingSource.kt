package com.rumble.domain.discover.model.datasource

import androidx.paging.PagingState
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.network.api.VideoApi
import com.rumble.network.queryHelpers.LiveVideoFront
import com.rumble.network.queryHelpers.Options
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class CategoryLiveVideosPagingSource(
    private val videoApi: VideoApi,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, Feed>() {

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
                val nextKey = params.key ?: 0
                val response = videoApi.fetchLiveVideos(
                    front = LiveVideoFront.Front.value,
                    offset = nextKey,
                    limit = loadSize
                )
                val videoEntities = if (response.isSuccessful) {
                    response.body()?.data?.items?.mapIndexed { index, video ->
                        video.getVideoEntity().copy(index = index + nextKey)
                    }
                        ?: emptyList()
                } else emptyList()

                LoadResult.Page(
                    data = videoEntities,
                    prevKey = null,
                    nextKey = if (videoEntities.isEmpty()) null else nextKey + loadSize
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
}