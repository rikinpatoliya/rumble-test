package com.rumble.domain.common.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity

sealed class PlayListResult {
    data class Success(val playList: PlayListEntity) : PlayListResult()
    data class Failure(val rumbleError: RumbleError) : PlayListResult()
}