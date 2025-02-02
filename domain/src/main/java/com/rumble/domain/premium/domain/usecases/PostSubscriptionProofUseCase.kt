package com.rumble.domain.premium.domain.usecases

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscription
import com.rumble.domain.premium.domain.domainmodel.PurchaseResult
import com.rumble.domain.premium.model.repository.PurchaseRepository
import com.rumble.network.di.AppId
import com.rumble.network.queryHelpers.SubscriptionSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PostSubscriptionProofUseCase @Inject constructor(
    private val purchaseRepository: PurchaseRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
    @AppId val appId: String,
    @ApplicationContext private val context: Context,
) : RumbleUseCase {

    suspend operator fun invoke(
        purchaseToken: String,
        videoId: Long?,
        creatorId: String?,
        source: SubscriptionSource?
    ): PurchaseResult {
        val result = purchaseRepository.purchaseSubscription(
            appId = appId,
            productId = PremiumSubscription.SUBSCRIPTION_ID,
            purchaseToken = purchaseToken,
            appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: "",
            videoId = videoId,
            creatorId = creatorId,
            source = source,
        )
        if (result is PurchaseResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}