package com.rumble.domain.livechat.domain.domainmodel

data class RantEntity(
    val messageEntity: LiveChatMessageEntity,
    val timeLeftPercentage: Float
)