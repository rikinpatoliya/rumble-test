package com.rumble.domain.channels.model.datasource

import androidx.paging.PagingState
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.common.model.datasource.RumblePagingSource
import com.rumble.domain.feed.model.getChannelDetailsEntity
import com.rumble.network.api.ChannelApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class FollowedChannelsPagingSource(
    private val id: String? = null,
    private val channelApi: ChannelApi,
    private val dispatcher: CoroutineDispatcher,
) : RumblePagingSource<Int, ChannelDetailsEntity>() {

    private var nextKey = 0
    private var offset = 0

    override fun getRefreshKey(state: PagingState<Int, ChannelDetailsEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChannelDetailsEntity> =
        withContext(dispatcher) {
            val loadSize = getLoadSize(params.loadSize)
            try {
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

    private suspend fun fetchChannelVideoList(loadSize: Int): List<ChannelDetailsEntity> {
        val response = channelApi.listOfFollowedChannels(id, offset, loadSize).body()
        val items = response?.data?.items?.map { it.getChannelDetailsEntity() } ?: emptyList()
        offset += loadSize
        return items
    }

}