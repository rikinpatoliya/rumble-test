package com.rumble.domain.premium.model.repository

import com.rumble.domain.premium.domain.domainmodel.PurchaseResult
import com.rumble.network.queryHelpers.SubscriptionSource

interface PurchaseRepository {
    suspend fun purchaseSubscription(
        productId: String,
        purchaseToken: String,
        appId: String,
        appsFlyerId: String,
        videoId: Long?,
        creatorId: String?,
        source: SubscriptionSource?,
    ): PurchaseResult

    suspend fun purchaseGift(
        productId: String,
        purchaseToken: String,
        appId: String,
        videoId: Long
    ): PurchaseResult
}