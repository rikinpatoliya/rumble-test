package com.rumble.domain.common.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity

sealed class VideoListResult {
    data class Success(val videoList: List<VideoEntity>) : VideoListResult()
    data class Failure(val rumbleError: RumbleError) : VideoListResult()
}
