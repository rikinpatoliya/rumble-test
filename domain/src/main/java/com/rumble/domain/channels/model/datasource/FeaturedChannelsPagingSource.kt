package com.rumble.domain.channels.model.datasource

import androidx.paging.PagingState
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.model.getCreatorEntity
import com.rumble.network.api.ChannelApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class FeaturedChannelsPagingSource(
    private val id: String? = null,
    private val channelApi: ChannelApi,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, CreatorEntity>() {

    private var nextKey = 0
    private var offset = 0

    override fun getRefreshKey(state: PagingState<Int, CreatorEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CreatorEntity> =
        withContext(dispatcher) {
            try {
                val loadSize = getLoadSize(params.loadSize)
                nextKey = params.key ?: 0
                val items = fetchChannelVideoList(loadSize)

                LoadResult.Page(
                    data = items,
                    prevKey = null,
                    nextKey = if (items.isEmpty()) null else nextKey + loadSize
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

    override val keyReuseSupported: Boolean
        get() = true

    private suspend fun fetchChannelVideoList(loadSize: Int): List<CreatorEntity> {
        val response = channelApi.fetchFeaturedChannels(offset, loadSize).body()
        val items = response?.data?.items?.map { it.getCreatorEntity() } ?: emptyList()
        offset += loadSize
        return items
    }

}