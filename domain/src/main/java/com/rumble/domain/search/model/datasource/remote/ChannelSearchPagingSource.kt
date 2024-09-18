package com.rumble.domain.search.model.datasource.remote

import androidx.paging.PagingState
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.model.getChannelDetailsEntity
import com.rumble.network.api.SearchApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ChannelSearchPagingSource(
    private val query: String,
    private val searchApi: SearchApi,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, ChannelDetailsEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ChannelDetailsEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChannelDetailsEntity> =
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
                    response.body()?.data?.items?.map { it.getChannelDetailsEntity() }
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