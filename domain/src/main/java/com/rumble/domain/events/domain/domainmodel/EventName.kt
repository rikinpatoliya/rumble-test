package com.rumble.domain.events.domain.domainmodel

private const val eventVersion = "" //Will be changed in the future

enum class EventName(val value: String) {
    WATCH_PROGRESS("watch_progress$eventVersion")
}