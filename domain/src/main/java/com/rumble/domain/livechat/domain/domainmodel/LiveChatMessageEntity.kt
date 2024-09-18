package com.rumble.domain.livechat.domain.domainmodel

import androidx.compose.ui.graphics.Color
import java.math.BigDecimal
import java.time.LocalDateTime

data class LiveChatMessageEntity(
    val messageId: Long,
    val userId: Long,
    val channelId: Long? = null,
    val userName: String,
    val userThumbnail: String?,
    val message: String,
    val background: Color? = null,
    val titleBackground: Color? = null,
    val textColor: Color? = null,
    val rantPrice: BigDecimal? = null,
    val timeReceived: LocalDateTime,
    val currencySymbol: String,
    val badges: List<String>,
    val deleted: Boolean = false,
    val atMentionRange: IntRange? = null,
    val isNotification: Boolean = false,
    val notification: String? = null,
    val notificationBadge: String? = null,
    val userNameColor: Color? = null
)