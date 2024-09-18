package com.rumble.domain.feed.domain.domainmodel.ads

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult

sealed class RumbleAdResult {
    data class RumbleAdSuccess(val rumbleAdEntity: RumbleAdEntity) : RumbleAdResult()
    data class RumbleAdError(override val rumbleError: RumbleError?): RumbleAdResult(), RumbleResult
    data class RumbleUncaughtError(val tag: String, val exception: Exception): RumbleAdResult()
}
