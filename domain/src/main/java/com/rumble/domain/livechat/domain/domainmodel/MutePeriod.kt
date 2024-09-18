package com.rumble.domain.livechat.domain.domainmodel

enum class MutePeriod(val duration: Int? = null) {
    FiveMinutes(300),
    LiveStreamDuration,
    Forever
}