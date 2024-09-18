package com.rumble.domain.common.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity

sealed class FollowPlayListResult {
    object Success : FollowPlayListResult()
    data class Failure(val rumbleError: RumbleError) : FollowPlayListResult()
}