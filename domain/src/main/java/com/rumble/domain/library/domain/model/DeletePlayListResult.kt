package com.rumble.domain.library.domain.model

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity

sealed class DeletePlayListResult {
    object Success : DeletePlayListResult()
    data class Failure(val rumbleError: RumbleError) : DeletePlayListResult()
}