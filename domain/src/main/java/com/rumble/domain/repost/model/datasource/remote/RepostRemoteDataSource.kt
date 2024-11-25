package com.rumble.domain.repost.model.datasource.remote

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.dto.repost.DeleteRepostResponse
import com.rumble.network.dto.repost.RepostResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepostRemoteDataSource {
    fun fetchFeedRepostData(pageSize: Int): Flow<PagingData<Feed>>
    fun fetchRepostData(userId: String, channelId: String, pageSize: Int): Flow<PagingData<Feed>>
    suspend fun deleteRepost(repostId: Long): Response<DeleteRepostResponse>
    suspend fun addRepost(videoId: Long, channelId: Long?, message: String): Response<RepostResponse>
}