package com.rumble.domain.search.model.datasource.remote

import androidx.paging.PagingState
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.model.getCreatorEntity
import com.rumble.network.api.SearchApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ChannelSearchPagingSource(
    private val query: String,
    private val searchApi: SearchApi,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, CreatorEntity>() {

    override fun getRefreshKey(state: PagingState<Int, CreatorEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CreatorEntity> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
                val nextKey = params.key ?: 0
                val response =
                    searchApi.channelSearch(
                        query = query,
                        offset = nextKey,
                        limit = loadSize
                    )
                val channels = if (response.isSuccessful) {
                    response.body()?.data?.items?.map { it.getCreatorEntity() }
                        ?: emptyList()
                } else emptyList()

                LoadResult.Page(
                    data = channels,
                    prevKey = null,
                    nextKey = if (channels.isEmpty()) null else nextKey + loadSize
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
}