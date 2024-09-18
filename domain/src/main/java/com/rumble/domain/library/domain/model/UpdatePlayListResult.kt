package com.rumble.domain.library.domain.model

import com.rumble.domain.common.domain.domainmodel.RemoveFromPlaylistResult
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity

sealed class UpdatePlayListResult {
    data class Success(val playListEntity: PlayListEntity) : UpdatePlayListResult()
    object PlaylistCountLimitReached : UpdatePlayListResult()
    data class Failure(val rumbleError: RumbleError) : UpdatePlayListResult()
}