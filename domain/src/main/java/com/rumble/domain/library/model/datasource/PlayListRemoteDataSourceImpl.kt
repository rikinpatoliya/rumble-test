package com.rumble.domain.library.model.datasource

import androidx.paging.Pager
import androidx.paging.PagingData
import com.rumble.domain.common.model.getRumblePagingConfig
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.network.api.VideoApi
import com.rumble.network.dto.video.AddVideoToPlaylistResponse
import com.rumble.network.dto.video.ClearWatchHistoryResponse
import com.rumble.network.dto.video.DeletePlayListResponse
import com.rumble.network.dto.video.FollowPlayListResponse
import com.rumble.network.dto.video.PlayListResponse
import com.rumble.network.dto.video.PlayListsResponse
import com.rumble.network.dto.video.RemoveFromPlaylistResponse
import com.rumble.network.dto.video.UpdatePlayListResponse
import com.rumble.network.dto.video.VideoListResponse
import com.rumble.network.queryHelpers.PlayListInclude
import com.rumble.network.queryHelpers.PlayListType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class PlayListRemoteDataSourceImpl(
    private val videoApi: VideoApi,
    private val dispatcher: CoroutineDispatcher,
) : PlayListRemoteDataSource {

    override fun fetchPlayListVideosPaged(
        type: PlayListType,
        pageSize: Int,
    ): Flow<PagingData<Feed>> {
        return fetchPlayListVideosPaged(id = type.id, pageSize = pageSize)
    }

    override fun fetchPlayListVideosPaged(
        id: String,
        pageSize: Int,
    ): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(
                pageSize = pageSize,
            ),
            pagingSourceFactory = {
                PlayListVideoPagingSource(
                    playListId = id,
                    videoApi = videoApi,
                    dispatcher = dispatcher,
                )
            }).flow
    }

    override fun fetchPurchasedVideosPaged(
        pageSize: Int,
    ): Flow<PagingData<Feed>> {
        return Pager(
            config = getRumblePagingConfig(
                pageSize = pageSize,
            ),
            pagingSourceFactory = {
                PurchasesPagingSource(
                    videoApi = videoApi,
                    dispatcher = dispatcher,
                )
            }).flow
    }

    override fun fetchPlayListsPaged(pageSize: Int, videoIds: List<Long>?): Flow<PagingData<PlayListEntity>> {
        return Pager(
            config = getRumblePagingConfig(
                pageSize = pageSize,
            ),
            pagingSourceFactory = {
                PlayListPagingSource(
                    videoApi = videoApi,
                    dispatcher = dispatcher,
                    videoIds = videoIds
                )
            }).flow
    }

    override suspend fun fetchPurchasedVideos(
    ): Response<VideoListResponse> = videoApi.fetchPurchases()

    override suspend fun fetchPlayList(
        playListId: String,
    ): Response<PlayListResponse> = videoApi.fetchPlayList(playListId = playListId)

    override suspend fun addVideoToPlaylist(
        playlistId: String,
        videoId: Long,
    ): Response<AddVideoToPlaylistResponse> =
        videoApi.addVideoToPlaylist(playlistId = playlistId, videoId = videoId)

    override suspend fun removeVideoFromPlaylist(
        playlistId: String,
        videoId: Long,
    ): Response<RemoveFromPlaylistResponse> =
        videoApi.removeVideoToPlaylist(playlistId = playlistId, videoId = videoId)

    override suspend fun fetchPlayLists(): Response<PlayListsResponse> =
        videoApi.fetchPlayLists(include = PlayListInclude.All)

    override suspend fun followPlayList(playlistId: String): Response<FollowPlayListResponse> =
        videoApi.followPlayList(playlistId = playlistId)

    override suspend fun unFollowPlayList(playlistId: String): Response<FollowPlayListResponse> =
        videoApi.unFollowPlayList(playlistId = playlistId)

    override suspend fun deletePlayList(playlistId: String): Response<DeletePlayListResponse> =
        videoApi.deletePlayList(playlistId = playlistId)

    override suspend fun clearWatchHistory(): Response<ClearWatchHistoryResponse> =
        videoApi.clearWatchHistory()

    override suspend fun addPlayList(
        title: String,
        description: String?,
        visibility: String?,
        channelId: Long?
    ): Response<UpdatePlayListResponse> =
        videoApi.addPlayList(
            title = title,
            description = description,
            visibility = visibility,
            channelId = channelId
        )

    override suspend fun editPlayList(
        playListId: String,
        title: String,
        description: String?,
        visibility: String?,
        channelId: Long?
    ): Response<UpdatePlayListResponse> =
        videoApi.editPlayList(
            playlistId = playListId,
            title = title,
            description = description,
            visibility = visibility,
            channelId = channelId
        )
}