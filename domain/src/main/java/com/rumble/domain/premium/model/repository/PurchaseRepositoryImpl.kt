package com.rumble.domain.premium.model.repository

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.premium.domain.domainmodel.PurchaseResult
import com.rumble.domain.premium.model.datasource.PurchaseRemoteDataSource
import com.rumble.network.dto.purchase.PurchaseBody
import com.rumble.network.dto.purchase.PurchaseBodyData
import com.rumble.network.dto.purchase.SubscriptionBody
import com.rumble.network.dto.purchase.SubscriptionBodyData
import com.rumble.network.queryHelpers.SubscriptionSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

private const val TAG = "PurchaseRepositoryImpl"

class PurchaseRepositoryImpl(
    private val purchaseRemoteDataSource: PurchaseRemoteDataSource,
    private val dispatcher: CoroutineDispatcher
) : PurchaseRepository {

    override suspend fun purchaseSubscription(
        productId: String,
        purchaseToken: String,
        appId: String,
        appsFlyerId: String,
        videoId: Long?,
        creatorId: String?,
        source: SubscriptionSource?,
    ): PurchaseResult = withContext(dispatcher) {
        val bodyData = SubscriptionBodyData(
            productId = productId,
            purchaseToken = purchaseToken,
            packageName = appId,
            installationId = appsFlyerId,
            videoId = videoId,
            source = source?.value,
            creatorId = creatorId,
        )
        val response =
            purchaseRemoteDataSource.purchasePremiumSubscription(SubscriptionBody(data = bodyData))
        if (response.isSuccessful) {
            if (response.body()?.purchaseData?.success == true)
                PurchaseResult.Success
            else
                PurchaseResult.PurchaseFailure(errorMessage = response.body()?.purchaseErrors?.firstOrNull()?.errorMessage)
        } else {
            PurchaseResult.Failure(RumbleError(TAG, response.raw()))
        }
    }

    override suspend fun purchaseGift(
        productId: String,
        purchaseToken: String,
        appId: String,
        appsFlyerId: String,
        videoId: Long,
        channelId: Long?
    ): PurchaseResult = withContext(dispatcher) {
        val bodyData = PurchaseBodyData(
            productId = productId,
            purchaseToken = purchaseToken,
            packageName = appId,
            installationId = appsFlyerId,
            videoId = videoId,
            channelId = channelId,
        )
        val response =
            purchaseRemoteDataSource.purchaseGift(PurchaseBody(data = bodyData))
        if (response.isSuccessful) {
            if (response.body()?.purchaseData?.success == true)
                PurchaseResult.Success
            else
                PurchaseResult.PurchaseFailure(errorMessage = response.body()?.purchaseErrors?.firstOrNull()?.errorMessage)
        } else {
            PurchaseResult.Failure(RumbleError(TAG, response.raw()))
        }
    }
}