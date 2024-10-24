package com.rumble.domain.livechat.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult

data class LiveChatResult(
    val success: Boolean = true,
    val initialConfig: Boolean = false,
    val messageList: List<LiveChatMessageEntity> = emptyList(),
    val deletedMessageIdList: List<Long> = emptyList(),
    val mutedUserIdList: List<Long> = emptyList(),
    val liveChatConfig: LiveChatConfig? = null,
    val pinnedMessageId: Long? = null,
    val unpinnedMessageId: Long? = null,
    val canModerate: Boolean? = null,
    val liveGate: LiveGateEntity? = null,
    override val rumbleError: RumbleError? = null
) : RumbleResult