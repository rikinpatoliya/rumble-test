package com.rumble.domain.livechat.domain.domainmodel

data class RantConfig(
    val levelList: List<RantLevel>,
    val rantsEnabled: Boolean
)
