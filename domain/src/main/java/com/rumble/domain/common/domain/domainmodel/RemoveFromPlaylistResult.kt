package com.rumble.domain.common.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class RemoveFromPlaylistResult {
    object Success : RemoveFromPlaylistResult()
    object FailureVideoNotInPlaylist : RemoveFromPlaylistResult()
    data class Failure(val rumbleError: RumbleError) : RemoveFromPlaylistResult()
}