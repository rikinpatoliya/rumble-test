package com.rumble.domain.premium.model.datasource

import com.rumble.network.api.PurchaseApi
import com.rumble.network.dto.purchase.PurchaseBody
import com.rumble.network.dto.purchase.PurchaseResponse
import com.rumble.network.dto.purchase.SubscriptionBody
import retrofit2.Response

class PurchaseRemoteDataSourceImpl(private val purchaseApi: PurchaseApi) :
    PurchaseRemoteDataSource {

    override suspend fun purchasePremiumSubscription(body: SubscriptionBody): Response<PurchaseResponse> =
        purchaseApi.purchasePremium(body)

    override suspend fun purchaseGift(body: PurchaseBody): Response<PurchaseResponse> =
        purchaseApi.purchaseGift(body)
}