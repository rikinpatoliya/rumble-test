package com.rumble.domain.repost.model.repository

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.repost.domain.domainmodel.AddRepostResult
import com.rumble.domain.repost.domain.domainmodel.DeleteRepostResult
import kotlinx.coroutines.flow.Flow

interface RepostRepository {
    fun fetchFeedRepostData(pageSize: Int): Flow<PagingData<Feed>>
    fun fetchRepostData(userId: String, channelId: String, pageSize: Int): Flow<PagingData<Feed>>
    suspend fun deleteRepost(repostId: Long): DeleteRepostResult
    suspend fun addRepost(videoId: Long, channelId: Long?, message: String): AddRepostResult
}