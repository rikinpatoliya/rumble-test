package com.rumble.domain.livechat.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class MuteUserResult {
    object Success: MuteUserResult()
    data class MuteFailure(val errorMessage: String, val muteError: RumbleError): MuteUserResult()
    data class Failure(val error: RumbleError): MuteUserResult()
}