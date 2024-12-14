package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class LiveChatConfig(
    @SerializedName("rants")
    val rants: LiveChatConfigRant,
    @SerializedName("badges")
    val badges: Map<String, Badge>?,
    @SerializedName("message_length_max")
    val messageMaxLength: Int?,
    @SerializedName("gifts")
    val giftList: PremiumGiftList?,
)
