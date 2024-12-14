package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class PremiumGift(
    @SerializedName("id")
    val id: String,
    @SerializedName("amount_cents")
    val amountCents: Int,
    @SerializedName("total_gifts")
    val totalGifts: Int,
)