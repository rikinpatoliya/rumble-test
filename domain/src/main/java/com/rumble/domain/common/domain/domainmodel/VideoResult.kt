package com.rumble.domain.common.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity

sealed class VideoResult {
    data class Success(val video: VideoEntity) : VideoResult()
    data class Failure(val rumbleError: RumbleError) : VideoResult()
}