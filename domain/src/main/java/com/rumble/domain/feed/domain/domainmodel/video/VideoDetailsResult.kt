package com.rumble.domain.feed.domain.domainmodel.video

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult

sealed class VideoDetailsResult {

    data class VideoDetailsSuccess(val videoEntity: VideoEntity) : VideoDetailsResult()

    data class VideoDetailsError(override val rumbleError: RumbleError?) : VideoDetailsResult(),
        RumbleResult

}