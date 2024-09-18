package com.rumble.domain.events.domain.domainmodel

sealed class SendWatchProgressEventsResult {
    data class Success(val eventEndpoint: String): SendWatchProgressEventsResult()
    object Failure: SendWatchProgressEventsResult()
}