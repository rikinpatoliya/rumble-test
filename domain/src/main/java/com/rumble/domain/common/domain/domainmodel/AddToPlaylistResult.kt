package com.rumble.domain.common.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.PlaylistVideoEntity

sealed class AddToPlaylistResult {
    data class Success(val playlistVideoEntity: PlaylistVideoEntity) : AddToPlaylistResult()
    object FailureAlreadyInPlaylist : AddToPlaylistResult()
    data class Failure(val rumbleError: RumbleError) : AddToPlaylistResult()
}
