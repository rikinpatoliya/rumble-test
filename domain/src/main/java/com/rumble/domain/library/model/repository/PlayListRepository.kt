package com.rumble.domain.library.model.repository

import androidx.paging.PagingData
import com.rumble.domain.common.domain.domainmodel.AddToPlaylistResult
import com.rumble.domain.common.domain.domainmodel.FollowPlayListResult
import com.rumble.domain.common.domain.domainmodel.PlayListResult
import com.rumble.domain.common.domain.domainmodel.PlayListsResult
import com.rumble.domain.common.domain.domainmodel.RemoveFromPlaylistResult
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.library.domain.model.ClearWatchHistoryResult
import com.rumble.domain.library.domain.model.DeletePlayListResult
import com.rumble.domain.library.domain.model.UpdatePlayListResult
import com.rumble.network.queryHelpers.PlayListType
import kotlinx.coroutines.flow.Flow

interface PlayListRepository {
    suspend fun fetchPurchases(pageSize: Int): VideoListResult
    fun fetchPurchasesFlow(pageSize: Int): Flow<PagingData<Feed>>
    fun fetchPlayListVideosPaged(type: PlayListType, pageSize: Int): Flow<PagingData<Feed>>
    fun fetchPlayListVideosPaged(id: String, pageSize: Int): Flow<PagingData<Feed>>
    suspend fun fetchPlayListVideos(playListId: String, pageSize: Int): VideoListResult
    suspend fun fetchPlayLists(pageSize: Int): PlayListsResult
    suspend fun fetchPlayList(playListId: String): PlayListResult
    suspend fun addVideoToPlaylist(playlistId: String, videoId: Long): AddToPlaylistResult
    suspend fun removeVideoFromPlaylist(playlistId: String, videoId: Long): RemoveFromPlaylistResult
    suspend fun followPlayList(playlistId: String, isFollowing: Boolean): FollowPlayListResult
    suspend fun deletePlayList(playlistId: String): DeletePlayListResult
    suspend fun clearWatchHistory(): ClearWatchHistoryResult
    suspend fun addPlayList(
        title: String,
        description: String?,
        visibility: String?,
        channelId: Long?
    ): UpdatePlayListResult

    suspend fun editPlayList(
        playListId: String,
        title: String,
        description: String?,
        visibility: String?,
        channelId: Long?
    ): UpdatePlayListResult
}