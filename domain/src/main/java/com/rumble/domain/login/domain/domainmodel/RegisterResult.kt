package com.rumble.domain.login.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class RegisterResult {
    object Success : RegisterResult()
    data class Failure(val error: RumbleError, val errorMessage: String?) : RegisterResult()
    data class DuplicatedRequest(val error: RumbleError): RegisterResult()
}