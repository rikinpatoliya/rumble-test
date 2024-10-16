package com.rumble.domain.premium.model.repository

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.premium.domain.domainmodel.SubscriptionResult
import com.rumble.domain.premium.model.datasource.SubscriptionRemoteDataSource
import com.rumble.network.dto.supscription.SubscriptionBody
import com.rumble.network.dto.supscription.SubscriptionBodyData
import com.rumble.network.queryHelpers.SubscriptionSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

private const val TAG = "SubscriptionRepositoryImpl"

class SubscriptionRepositoryImpl(
    private val subscriptionRemoteDataSource: SubscriptionRemoteDataSource,
    private val dispatcher: CoroutineDispatcher
) : SubscriptionRepository {

    override suspend fun purchaseSubscription(
        productId: String,
        purchaseToken: String,
        appId: String,
        appsFlyerId: String,
        videoId: Long?,
        source: SubscriptionSource?,
    ): SubscriptionResult = withContext(dispatcher) {
        val bodyData = SubscriptionBodyData(
            productId = productId,
            purchaseToken = purchaseToken,
            packageName = appId,
            installationId = appsFlyerId,
            videoId = videoId,
            source = source?.value
        )
        val response = subscriptionRemoteDataSource.purchasePremiumSubscription(SubscriptionBody(data = bodyData))
        if (response.isSuccessful) {
            if (response.body()?.subscriptionData?.success == true)
                SubscriptionResult.Success
            else
                SubscriptionResult.PurchaseFailure(errorMessage = response.body()?.subscriptionErrors?.firstOrNull()?.errorMessage)
        } else {
            SubscriptionResult.Failure(RumbleError(TAG, response.raw()))
        }
    }
}