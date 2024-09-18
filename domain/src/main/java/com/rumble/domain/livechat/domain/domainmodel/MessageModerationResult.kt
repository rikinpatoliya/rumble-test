package com.rumble.domain.livechat.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class MessageModerationResult {
    object Success: MessageModerationResult()
    data class Failure(val rumbleError: RumbleError): MessageModerationResult()
}