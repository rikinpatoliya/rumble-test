package com.rumble.battles.library.presentation.playlist

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlaylistVideoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

interface AddToPlayListHandler {
    val addToPlayListState: StateFlow<AddToPlayListState>
    val updatedPlaylist: StateFlow<UpdatePlaylist?>

    fun onCreateNewPlayList(videoId: Long)
    fun getIsVideoInPlayList(playListEntity: PlayListEntity, videoId: Long): Boolean
    fun onToggleVideoInPlayList(inPlayList: Boolean, playListId: String, videoId: Long)
    fun canSaveToPlayList(entity: PlayListEntity): Boolean
}

data class AddToPlayListState(
    val watchLaterPlayList: PlayListEntity = PlayListEntity(),
    val availablePlayLists: Flow<PagingData<PlayListEntity>> = emptyFlow(),
)

sealed class UpdatePlaylist {
    data class VideoAddedToPlaylist(
        val playlistId: String,
        val playlistVideoEntity: PlaylistVideoEntity,
    ) : UpdatePlaylist()

    data class VideoRemovedFromPlaylist(
        val playlistId: String,
        val videoId: Long,
    ) : UpdatePlaylist()
}