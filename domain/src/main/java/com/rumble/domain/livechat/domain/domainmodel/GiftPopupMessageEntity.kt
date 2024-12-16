package com.rumble.domain.livechat.domain.domainmodel

data class GiftPopupMessageEntity(
    val giftType: PremiumGiftType,
    val giftAuthor: String,
    val giftAuthorImage: String,
)