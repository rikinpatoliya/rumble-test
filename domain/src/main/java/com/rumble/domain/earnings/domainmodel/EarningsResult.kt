package com.rumble.domain.earnings.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class EarningsResult {
    data class Success(val earnings: EarningsEntity) : EarningsResult()
    data class Failure(val rumbleError: RumbleError) : EarningsResult()
}