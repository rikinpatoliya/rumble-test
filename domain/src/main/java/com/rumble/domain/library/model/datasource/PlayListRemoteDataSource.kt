package com.rumble.domain.library.model.datasource

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.network.dto.video.AddVideoToPlaylistResponse
import com.rumble.network.dto.video.ClearWatchHistoryResponse
import com.rumble.network.dto.video.DeletePlayListResponse
import com.rumble.network.dto.video.UpdatePlayListResponse
import com.rumble.network.dto.video.FollowPlayListResponse
import com.rumble.network.dto.video.PlayListResponse
import com.rumble.network.dto.video.PlayListsResponse
import com.rumble.network.dto.video.RemoveFromPlaylistResponse
import com.rumble.network.dto.video.VideoListResponse
import com.rumble.network.queryHelpers.PlayListType
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface PlayListRemoteDataSource {
    fun fetchPlayListVideosPaged(type: PlayListType, pageSize: Int): Flow<PagingData<Feed>>
    fun fetchPlayListVideosPaged(id: String, pageSize: Int): Flow<PagingData<Feed>>
    fun fetchPurchasedVideosPaged(pageSize: Int): Flow<PagingData<Feed>>
    fun fetchPlayListsPaged(pageSize: Int, videoIds: List<Long>? = null): Flow<PagingData<PlayListEntity>>
    suspend fun fetchPurchasedVideos(): Response<VideoListResponse>
    suspend fun fetchPlayList(playListId: String): Response<PlayListResponse>
    suspend fun fetchPlayLists(): Response<PlayListsResponse>
    suspend fun addVideoToPlaylist(
        playlistId: String,
        videoId: Long
    ): Response<AddVideoToPlaylistResponse>

    suspend fun removeVideoFromPlaylist(
        playlistId: String,
        videoId: Long
    ): Response<RemoveFromPlaylistResponse>

    suspend fun followPlayList(playlistId: String): Response<FollowPlayListResponse>
    suspend fun unFollowPlayList(playlistId: String): Response<FollowPlayListResponse>
    suspend fun deletePlayList(playlistId: String): Response<DeletePlayListResponse>
    suspend fun clearWatchHistory(): Response<ClearWatchHistoryResponse>
    suspend fun addPlayList(
        title: String,
        description: String?,
        visibility: String?,
        channelId: Long?
    ): Response<UpdatePlayListResponse>
    suspend fun editPlayList(
        playListId: String,
        title: String,
        description: String?,
        visibility: String?,
        channelId: Long?
    ): Response<UpdatePlayListResponse>
}