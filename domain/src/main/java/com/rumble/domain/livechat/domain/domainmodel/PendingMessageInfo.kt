package com.rumble.domain.livechat.domain.domainmodel

data class PendingMessageInfo(
    val chatId: Long,
    val requestId: String,
    val pendingMessageId: Long
)
