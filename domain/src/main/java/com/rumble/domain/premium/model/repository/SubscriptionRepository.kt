package com.rumble.domain.premium.model.repository

import com.rumble.domain.premium.domain.domainmodel.SubscriptionResult
import com.rumble.network.queryHelpers.SubscriptionSource

interface SubscriptionRepository {
    suspend fun purchaseSubscription(
        productId: String,
        purchaseToken: String,
        appId: String,
        appsFlyerId: String,
        videoId: Long?,
        creatorId: String?,
        source: SubscriptionSource?,
    ): SubscriptionResult
}