package com.rumble.network.api

import com.rumble.network.dto.supscription.SubscriptionBody
import com.rumble.network.dto.supscription.SubscriptionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SubscriptionApi {

    @POST("purchase/subscription")
    suspend fun purchasePremium(@Body body: SubscriptionBody): Response<SubscriptionResponse>
}