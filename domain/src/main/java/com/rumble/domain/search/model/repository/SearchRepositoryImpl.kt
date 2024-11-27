package com.rumble.domain.search.model.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.common.model.getRumblePagingConfig
import com.rumble.domain.discover.model.getCategoryEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.model.getCreatorEntity
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.domain.search.domain.domainModel.AutoCompleteQueriesResult
import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.domain.search.domain.domainModel.SearchResult
import com.rumble.domain.search.model.datasource.local.QueryDao
import com.rumble.domain.search.model.datasource.local.RoomQuery
import com.rumble.domain.search.model.datasource.remote.ChannelSearchPagingSource
import com.rumble.domain.search.model.datasource.remote.VideoSearchPagingSource
import com.rumble.domain.sort.DurationType
import com.rumble.domain.sort.FilterType
import com.rumble.domain.sort.SortType
import com.rumble.network.api.SearchApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset

class SearchRepositoryImpl(
    private val queryDao: QueryDao,
    private val searchApi: SearchApi,
    private val dispatcher: CoroutineDispatcher,
) : SearchRepository {

    override suspend fun saveQuery(recentQuery: RecentQuery) = withContext(dispatcher) {
        queryDao.getQuery(recentQuery.query)?.let {
            updateQuery(recentQuery.copy(id = it.id ?: 0))
        } ?: run {
            queryDao.saveQuery(
                RoomQuery(
                    query = recentQuery.query,
                    time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                )
            )
        }
    }

    override suspend fun updateQuery(recentQuery: RecentQuery) = withContext(dispatcher) {
        queryDao.updateQuery(
            RoomQuery(
                id = recentQuery.id,
                query = recentQuery.query,
                time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            )
        )
    }

    override suspend fun deleteQuery(recentQuery: RecentQuery) = withContext(dispatcher) {
        queryDao.deleteQuery(
            RoomQuery(
                id = recentQuery.id,
                query = recentQuery.query,
                time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            )
        )
    }

    override suspend fun deleteAllQueries() = withContext(dispatcher) {
        queryDao.deleteAllQueries()
    }

    override suspend fun getAllQueries(): List<RecentQuery> =
        queryDao.getAll().map { RecentQuery(id = it.id ?: 0, query = it.query) }

    override suspend fun filterQueries(filter: String): List<RecentQuery> =
        queryDao.filter("%$filter%").map { RecentQuery(id = it.id ?: 0, query = it.query) }

    override suspend fun searchCombined(
        query: String,
        sort: SortType?,
        filter: FilterType?,
        duration: DurationType?
    ): SearchResult {
        val result = searchApi.combinedSearch(
            query = query,
            sort = sort?.sortQuery,
            date = filter?.dateFilter,
            duration = duration?.duration
        )
        return if (result.isSuccessful) {
            SearchResult(
                channelList = result.body()?.data?.channel?.items?.map { it.getCreatorEntity() }
                    ?: emptyList(),
                videoList = result.body()?.data?.video?.items?.mapIndexed { index, video ->
                    video.getVideoEntity().copy(index = index)
                }
                    ?: emptyList())
        } else SearchResult()
    }

    override fun searchChannels(query: String): Flow<PagingData<CreatorEntity>> = Pager(
        config = getRumblePagingConfig(),
        pagingSourceFactory = {
            ChannelSearchPagingSource(
                query = query,
                searchApi = searchApi,
                dispatcher = dispatcher
            )
        }).flow

    override fun searchVideos(
        query: String,
        sort: SortType?,
        filter: FilterType?,
        duration: DurationType?,
        pageSize: Int,
    ): Flow<PagingData<VideoEntity>> = Pager(
        config = getRumblePagingConfig(
            pageSize = pageSize
        ),
        pagingSourceFactory = {
            VideoSearchPagingSource(
                query = query,
                sort = sort,
                filter = filter,
                duration = duration,
                searchApi = searchApi,
                dispatcher = dispatcher
            )
        }).flow

    override suspend fun getAutoCompleteQueries(query: String): AutoCompleteQueriesResult {
        val result = searchApi.searchAutoComplete(
            query = query,
        )
        return if (result.isSuccessful) {
            AutoCompleteQueriesResult(
                channelList = result.body()?.data?.channels?.map { it.getCreatorEntity() }
                    ?: emptyList(),
                categoryList = result.body()?.data?.categories?.map { it.getCategoryEntity() }
                    ?: emptyList())
        } else AutoCompleteQueriesResult()
    }
}
