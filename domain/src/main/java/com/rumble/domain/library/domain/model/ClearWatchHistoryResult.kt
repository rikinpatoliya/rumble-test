package com.rumble.domain.library.domain.model

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity

sealed class ClearWatchHistoryResult {
    object Success : ClearWatchHistoryResult()
    data class Failure(val rumbleError: RumbleError) : ClearWatchHistoryResult()
}