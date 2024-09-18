package com.rumble.domain.video.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity

sealed class FetchRelatedVideoListResult {
    data class Success(val videoList: List<VideoEntity>) : FetchRelatedVideoListResult()
    data class Failure(val rumbleError: RumbleError) : FetchRelatedVideoListResult()
}
