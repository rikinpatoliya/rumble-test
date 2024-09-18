package com.rumble.domain.common.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class EmptyResult {
    object Success : EmptyResult()
    data class Failure(val rumbleError: RumbleError) : EmptyResult()
}