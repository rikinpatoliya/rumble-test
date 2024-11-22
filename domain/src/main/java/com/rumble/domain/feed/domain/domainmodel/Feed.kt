package com.rumble.domain.feed.domain.domainmodel

interface Feed {
    val index: Int
    val id: Long
        get() = 0
}