package com.rumble.domain.premium.model.datasource

import com.rumble.network.dto.purchase.PurchaseBody
import com.rumble.network.dto.purchase.PurchaseResponse
import com.rumble.network.dto.purchase.SubscriptionBody
import retrofit2.Response

interface PurchaseRemoteDataSource {
    suspend fun purchasePremiumSubscription(body: SubscriptionBody): Response<PurchaseResponse>
    suspend fun purchaseGift(body: PurchaseBody): Response<PurchaseResponse>
}