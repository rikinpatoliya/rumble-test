package com.rumble.domain.premium.model.datasource

import com.rumble.network.dto.supscription.SubscriptionBody
import com.rumble.network.dto.supscription.SubscriptionResponse
import retrofit2.Response

interface SubscriptionRemoteDataSource {
    suspend fun purchasePremiumSubscription(body: SubscriptionBody): Response<SubscriptionResponse>
}