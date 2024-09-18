package com.rumble.domain.livechat.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class LiveChatMessageResult {
    object MessageSuccess : LiveChatMessageResult()

    data class RantMessageSuccess(val pendingMessageInfo: PendingMessageInfo) :
        LiveChatMessageResult()

    data class Failure(val userErrorMessage: String? = null, val rumbleError: RumbleError? = null) :
        LiveChatMessageResult()
}