package com.rumble.domain.login.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class ResetPasswordResult {
    object Success : ResetPasswordResult()
    data class Failure(val rumbleError: RumbleError) : ResetPasswordResult()
}
