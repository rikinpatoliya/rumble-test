package com.rumble.domain.livechat.domain.domainmodel

import com.android.billingclient.api.ProductDetails

enum class PremiumGiftType {
    Rumble,
    Premium;

    companion object {
        fun getByStringValue(stringValue: String): PremiumGiftType =
            if (stringValue == "premium") Premium else Rumble
    }
}

data class PremiumGiftEntity(
    val type: PremiumGiftType,
    val giftList: List<PremiumGiftDetails>,
)

data class PremiumGiftDetails(
    val productId: String,
    val priceCents: Int,
    val giftsAmount: Int,
    val productDetails: ProductDetails? = null
)