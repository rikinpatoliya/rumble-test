package com.rumble.domain.premium.model.datasource

import com.rumble.network.api.SubscriptionApi
import com.rumble.network.dto.supscription.SubscriptionBody
import com.rumble.network.dto.supscription.SubscriptionResponse
import retrofit2.Response

class SubscriptionRemoteDataSourceImpl(private val subscriptionApi: SubscriptionApi) : SubscriptionRemoteDataSource {

    override suspend fun purchasePremiumSubscription(body: SubscriptionBody): Response<SubscriptionResponse> =
        subscriptionApi.purchasePremium(body)
}