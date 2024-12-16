package com.rumble.domain.livechat.domain.domainmodel

import androidx.compose.ui.graphics.Color
import java.math.BigDecimal
import java.time.LocalDateTime

data class LiveChatMessageEntity(
    val messageId: Long = 0,
    val userId: Long = 0,
    val channelId: Long? = null,
    val userName: String = "",
    val userThumbnail: String? = "",
    val message: String = "",
    val background: Color? = null,
    val titleBackground: Color? = null,
    val textColor: Color? = null,
    val rantPrice: BigDecimal? = null,
    val timeReceived: LocalDateTime = LocalDateTime.now(),
    val currencySymbol: String = "",
    val badges: List<String> = emptyList(),
    val deleted: Boolean = false,
    val atMentionRange: IntRange? = null,
    val isNotification: Boolean = false,
    val notification: String? = null,
    val notificationBadge: String? = null,
    val userNameColor: Color? = null,
    val isRaidMessage: Boolean = false,
    val raidMessageType: RaidMessageType? = null,
    val giftType: PremiumGiftType? = null,
    val creatorUserName: String? = null,
    val giftsAmount: Int? = null,
)