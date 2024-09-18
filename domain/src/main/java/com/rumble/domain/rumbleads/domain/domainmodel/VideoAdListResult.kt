package com.rumble.domain.rumbleads.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.videoplayer.domain.model.VideoAdDataEntity

sealed class VideoAdListResult {
    data class Success(val videoAdData: VideoAdDataEntity) : VideoAdListResult()
    data class Failure(val rumbleError: RumbleError) : VideoAdListResult()
    data class UncaughtError(val tag: String, val exception: Throwable) : VideoAdListResult()
    object EmptyVideoAdList : VideoAdListResult()
}
