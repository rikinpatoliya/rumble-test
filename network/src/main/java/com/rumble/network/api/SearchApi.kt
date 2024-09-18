package com.rumble.network.api

import com.rumble.network.dto.channel.ChannelsResponse
import com.rumble.network.dto.search.AutoCompleteResult
import com.rumble.network.dto.search.CombinedSearchResult
import com.rumble.network.dto.search.SearchVideoResult
import com.rumble.network.queryHelpers.Date
import com.rumble.network.queryHelpers.Duration
import com.rumble.network.queryHelpers.Options
import com.rumble.network.queryHelpers.Sort
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

    @GET("service.php?name=search")
    suspend fun combinedSearch(
        @Query("query") query: String,
        @Query("sort") sort: Sort? = null,
        @Query("date") date: Date? = null,
        @Query("duration") duration: Duration? = null
    ): Response<CombinedSearchResult>

    @GET("service.php?name=video_collection.search")
    suspend fun channelSearch(
        @Query("query") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<ChannelsResponse>

    @GET("service.php?name=media.search")
    suspend fun videosSearch(
        @Query("query") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("date") date: Date? = null,
        @Query("duration") duration: Duration? = null,
        @Query("sort") sort: Sort? = null,
        @Query("options") options: String = listOf(
            Options.WATCHING_PROGRESS
        ).joinToString(separator = ","),
    ): Response<SearchVideoResult>

    @GET("service.php?name=search.autocomplete")
    suspend fun searchAutoComplete(
        @Query("query") query: String,
    ): Response<AutoCompleteResult>
}