package com.rumble.domain.livechat.domain.domainmodel

data class LiveChatConfig(
    val chatId: Long,
    val rantConfig: RantConfig,
    val badges: Map<String, BadgeEntity>,
    val messageMaxLength: Int,
    val currentUserBadges: List<String>,
    val currencySymbol: String,
    val emoteList: List<EmoteEntity>? = null,
    val channels: List<LiveChatChannelEntity>,
)
