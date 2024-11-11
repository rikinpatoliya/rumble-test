package com.rumble.domain.livechat.domain.domainmodel

data class EmoteEntity(
    val name: String,
    val url: String,
    val subscribersOnly: Boolean = false,
    val locked: Boolean = false,
    val usageCount: Int = 0,
    val lastUsageTime: Long = 0,
)
