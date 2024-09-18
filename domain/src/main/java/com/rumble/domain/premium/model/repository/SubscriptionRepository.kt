package com.rumble.domain.premium.model.repository

import com.rumble.domain.premium.domain.domainmodel.SubscriptionResult

interface SubscriptionRepository {
    suspend fun purchaseSubscription(
        productId: String,
        purchaseToken: String,
        appId: String,
        appsFlyerId: String,
    ): SubscriptionResult
}