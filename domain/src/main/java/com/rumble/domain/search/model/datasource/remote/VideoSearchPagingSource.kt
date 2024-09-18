package com.rumble.domain.search.model.datasource.remote

import androidx.paging.PagingState
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.domain.sort.DurationType
import com.rumble.domain.sort.FilterType
import com.rumble.domain.sort.SortType
import com.rumble.network.api.SearchApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class VideoSearchPagingSource(
    private val query: String,
    private val searchApi: SearchApi,
    private val sort: SortType? = null,
    private val filter: FilterType? = null,
    private val duration: DurationType? = null,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, VideoEntity>() {

    override fun getRefreshKey(state: PagingState<Int, VideoEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VideoEntity> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
                val nextKey = params.key ?: 0
                val response = searchApi.videosSearch(
                    query,
                    nextKey,
                    loadSize,
                    sort = sort?.sortQuery,
                    date = filter?.dateFilter,
                    duration = duration?.duration
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