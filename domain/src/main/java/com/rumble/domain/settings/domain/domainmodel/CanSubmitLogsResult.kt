package com.rumble.domain.settings.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class CanSubmitLogsResult {
    data class Success(val canSubmitLogs: Boolean) : CanSubmitLogsResult()
    data class Failure(val rumbleError: RumbleError) : CanSubmitLogsResult()
}
