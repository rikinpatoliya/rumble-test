package com.rumble.domain.library.model.repository

import androidx.paging.PagingData
import com.rumble.domain.common.domain.domainmodel.AddToPlaylistResult
import com.rumble.domain.common.domain.domainmodel.FollowPlayListResult
import com.rumble.domain.common.domain.domainmodel.PlayListResult
import com.rumble.domain.common.domain.domainmodel.PlayListsResult
import com.rumble.domain.common.domain.domainmodel.RemoveFromPlaylistResult
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.model.getPlayListEntity
import com.rumble.domain.feed.model.getPlaylistVideoEntity
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.domain.library.domain.model.ClearWatchHistoryResult
import com.rumble.domain.library.domain.model.DeletePlayListResult
import com.rumble.domain.library.domain.model.UpdatePlayListResult
import com.rumble.domain.library.model.datasource.PlayListRemoteDataSource
import com.rumble.network.queryHelpers.PlayListType
import com.rumble.utils.RumbleConstants.HTTP_CONFLICT
import kotlinx.coroutines.flow.Flow

private const val TAG = "PlayListRepositoryImpl"

class PlayListRepositoryImpl(
    private val playlistRemoteDataSource: PlayListRemoteDataSource,
) : PlayListRepository {

    override suspend fun fetchPurchases(pageSize: Int): VideoListResult {
        val response = playlistRemoteDataSource.fetchPurchasedVideos()

        val body = response.body()
        return if (response.isSuccessful && body != null) {
            VideoListResult.Success(
                videoList = body.data.items.map {
                    it.getVideoEntity()
                }.take(pageSize)
            )
        } else {
            VideoListResult.Failure(
                rumbleError = RumbleError(
                    TAG,
                    response = response.raw()
                )
            )
        }
    }

    override fun fetchPurchasesFlow(pageSize: Int): Flow<PagingData<Feed>> =
        playlistRemoteDataSource.fetchPurchasedVideosPaged(pageSize = pageSize)

    override fun fetchPlayListVideosPaged(
        type: PlayListType,
        pageSize: Int,
    ): Flow<PagingData<Feed>> =
        playlistRemoteDataSource.fetchPlayListVideosPaged(type = type, pageSize = pageSize)

    override fun fetchPlayListVideosPaged(id: String, pageSize: Int): Flow<PagingData<Feed>> =
        playlistRemoteDataSource.fetchPlayListVideosPaged(id = id, pageSize = pageSize)

    override suspend fun fetchPlayListVideos(
        playListId: String,
        pageSize: Int,
    ): VideoListResult {
        val response = playlistRemoteDataSource.fetchPlayList(playListId)

        val body = response.body()
        return if (response.isSuccessful && body != null) {
            VideoListResult.Success(
                videoList = body.playList.items.map {
                    it.video.getVideoEntity()
                }.take(pageSize)
            )
        } else {
            VideoListResult.Failure(
                rumbleError = RumbleError(
                    TAG,
                    response = response.raw()
                )
            )
        }
    }

    override suspend fun fetchPlayLists(
        pageSize: Int
    ): PlayListsResult {
        val response = playlistRemoteDataSource.fetchPlayLists()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            PlayListsResult.Success(
                playListEntities = body.playListsData.playLists.map {
                    it.getPlayListEntity()
                }.take(pageSize)
            )
        } else {
            PlayListsResult.Failure(
                rumbleError = RumbleError(
                    TAG,
                    response = response.raw()
                )
            )
        }
    }

    override suspend fun fetchPlayList(playListId: String): PlayListResult {
        val response = playlistRemoteDataSource.fetchPlayList(playListId)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            PlayListResult.Success(
                playList = body.playList.getPlayListEntity()
            )
        } else {
            PlayListResult.Failure(
                rumbleError = RumbleError(
                    TAG,
                    response = response.raw()
                )
            )
        }
    }

    override suspend fun addVideoToPlaylist(playlistId: String, videoId: Long): AddToPlaylistResult {
        val response = playlistRemoteDataSource.addVideoToPlaylist(playlistId, videoId)
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            AddToPlaylistResult.Success(body.data.video.getPlaylistVideoEntity())
        } else if (response.code() == HTTP_CONFLICT) {
            AddToPlaylistResult.FailureAlreadyInPlaylist
        } else {
            AddToPlaylistResult.Failure(rumbleError = RumbleError(tag = TAG, response = response.raw()))
        }
    }

    override suspend fun removeVideoFromPlaylist(playlistId: String, videoId: Long): RemoveFromPlaylistResult {
        val response = playlistRemoteDataSource.removeVideoFromPlaylist(playlistId, videoId)
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            RemoveFromPlaylistResult.Success
        } else if (response.code() == HTTP_CONFLICT) {
            RemoveFromPlaylistResult.FailureVideoNotInPlaylist
        } else {
            RemoveFromPlaylistResult.Failure(rumbleError = RumbleError(tag = TAG, response = response.raw()))
        }
    }

    override suspend fun followPlayList(
        playlistId: String,
        isFollowing: Boolean,
    ): FollowPlayListResult {
        val response =
            if (isFollowing) playlistRemoteDataSource.followPlayList(playlistId) else playlistRemoteDataSource.unFollowPlayList(
                playlistId
            )
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            FollowPlayListResult.Success
        } else {
            FollowPlayListResult.Failure(
                rumbleError = RumbleError(
                    TAG,
                    response = response.raw()
                )
            )
        }
    }

    override suspend fun deletePlayList(playlistId: String): DeletePlayListResult {
        val response = playlistRemoteDataSource.deletePlayList(playlistId)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            DeletePlayListResult.Success
        } else {
            DeletePlayListResult.Failure(
                rumbleError = RumbleError(
                    TAG,
                    response = response.raw()
                )
            )
        }
    }

    override suspend fun clearWatchHistory(): ClearWatchHistoryResult {
        val response = playlistRemoteDataSource.clearWatchHistory()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            ClearWatchHistoryResult.Success
        } else {
            ClearWatchHistoryResult.Failure(
                rumbleError = RumbleError(
                    TAG,
                    response = response.raw()
                )
            )
        }
    }

    override suspend fun addPlayList(
        title: String,
        description: String?,
        visibility: String?,
        channelId: Long?
    ): UpdatePlayListResult {
        val response =
            playlistRemoteDataSource.addPlayList(title, description, visibility, channelId)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            UpdatePlayListResult.Success(
                playListEntity = body.data.getPlayListEntity()
            )
        } else if (response.code() == HTTP_CONFLICT) {
            UpdatePlayListResult.PlaylistCountLimitReached
        } else {
            UpdatePlayListResult.Failure(rumbleError = RumbleError(tag = TAG, response = response.raw()))
        }
    }

    override suspend fun editPlayList(
        playListId: String,
        title: String,
        description: String?,
        visibility: String?,
        channelId: Long?
    ): UpdatePlayListResult {
        val response = playlistRemoteDataSource.editPlayList(
            playListId,
            title,
            description,
            visibility,
            channelId
        )
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            UpdatePlayListResult.Success(
                playListEntity = body.data.getPlayListEntity()
            )
        } else {
            UpdatePlayListResult.Failure(rumbleError = RumbleError(tag = TAG, response = response.raw()))
        }
    }
}