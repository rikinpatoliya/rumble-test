package com.rumble.domain.timerange.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class SendTimeRangeListResult {
    object Success : SendTimeRangeListResult()
    data class Failure(val rumbleError: RumbleError) : SendTimeRangeListResult()
}
