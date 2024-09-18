package com.rumble.domain.common.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity

sealed class PlayListsResult {
    data class Success(val playListEntities: List<PlayListEntity>) : PlayListsResult()
    data class Failure(val rumbleError: RumbleError) : PlayListsResult()
}