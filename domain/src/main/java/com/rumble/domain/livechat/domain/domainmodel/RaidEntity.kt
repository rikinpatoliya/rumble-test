package com.rumble.domain.livechat.domain.domainmodel

data class RaidEntity(
    val currentChannelName: String = "",
    val currentChannelAvatar: String? = null,
    val targetUrl: String = "",
    val targetChannelName: String = "",
    val targetChannelAvatar: String? = null,
    val targetVideoTitle: String = "",
    val timeOut: Long = 0,
    val timePassed: Long = 0,
    val optedOut: Boolean = false,
)