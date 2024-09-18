package com.rumble.domain.search.model.repository

import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.search.domain.domainModel.AutoCompleteQueriesResult
import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.domain.search.domain.domainModel.SearchResult
import com.rumble.domain.sort.DurationType
import com.rumble.domain.sort.FilterType
import com.rumble.domain.sort.SortType
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun saveQuery(recentQuery: RecentQuery)
    suspend fun updateQuery(recentQuery: RecentQuery)
    suspend fun deleteQuery(recentQuery: RecentQuery)
    suspend fun deleteAllQueries()
    suspend fun getAllQueries(): List<RecentQuery>
    suspend fun filterQueries(filter: String): List<RecentQuery>
    suspend fun searchCombined(
        query: String, sort: SortType?,
        filter: FilterType?,
        duration: DurationType?,
    ): SearchResult

    fun searchChannels(query: String): Flow<PagingData<ChannelDetailsEntity>>
    fun searchVideos(
        query: String,
        sort: SortType? = null,
        filter: FilterType? = null,
        duration: DurationType? = null,
        pageSize: Int,
    ): Flow<PagingData<VideoEntity>>

    suspend fun getAutoCompleteQueries(query: String): AutoCompleteQueriesResult
}