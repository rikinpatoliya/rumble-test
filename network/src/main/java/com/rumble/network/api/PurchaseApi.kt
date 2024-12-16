package com.rumble.network.api

import com.rumble.network.dto.purchase.PurchaseBody
import com.rumble.network.dto.purchase.SubscriptionBody
import com.rumble.network.dto.purchase.PurchaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PurchaseApi {

    @POST("purchase/subscription")
    suspend fun purchasePremium(@Body body: SubscriptionBody): Response<PurchaseResponse>

    @POST("purchase/gift")
    suspend fun purchaseGift(@Body body: PurchaseBody): Response<PurchaseResponse>
}