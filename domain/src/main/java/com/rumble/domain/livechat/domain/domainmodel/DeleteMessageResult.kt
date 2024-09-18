package com.rumble.domain.livechat.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class DeleteMessageResult {
    object Success : DeleteMessageResult()
    data class Failure(val error: RumbleError?) : DeleteMessageResult()
}