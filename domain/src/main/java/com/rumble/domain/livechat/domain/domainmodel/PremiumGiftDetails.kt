package com.rumble.domain.livechat.domain.domainmodel

import com.android.billingclient.api.ProductDetails
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CommentAuthorEntity
import java.math.BigDecimal

enum class PremiumGiftType {
    SubsGift,
    PremiumGift;

    companion object {
        fun getByStringValue(stringValue: String): PremiumGiftType =
            if (stringValue == "premium") PremiumGift else SubsGift
    }
}

data class PremiumGiftEntity(
    val type: PremiumGiftType,
    val giftList: List<PremiumGiftDetails>,
)

data class PremiumGiftDetails(
    val productId: String,
    val priceCents: BigDecimal,
    val giftsAmount: Int,
    val productDetails: ProductDetails? = null
)

data class GiftPurchaseDetails(
    val premiumGiftDetails: PremiumGiftDetails,
    val premiumGiftType: PremiumGiftType,
    val authorEntity: CommentAuthorEntity?,
)