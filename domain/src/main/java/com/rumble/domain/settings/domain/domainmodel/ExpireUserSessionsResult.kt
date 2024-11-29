package com.rumble.domain.settings.domain.domainmodel

sealed class ExpireUserSessionsResult {
    data object Success: ExpireUserSessionsResult()
    data object Failure: ExpireUserSessionsResult()
}