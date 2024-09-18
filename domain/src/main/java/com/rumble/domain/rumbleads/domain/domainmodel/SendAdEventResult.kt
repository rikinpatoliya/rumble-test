package com.rumble.domain.rumbleads.domain.domainmodel

sealed class SendAdEventResult {
    object Success: SendAdEventResult()
    data class UncaughtError(val tag: String, val error: Throwable): SendAdEventResult()
}
