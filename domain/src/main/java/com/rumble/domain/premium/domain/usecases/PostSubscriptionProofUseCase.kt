package com.rumble.domain.premium.domain.usecases

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscription
import com.rumble.domain.premium.domain.domainmodel.SubscriptionResult
import com.rumble.domain.premium.model.repository.SubscriptionRepository
import com.rumble.network.di.AppId
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PostSubscriptionProofUseCase @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
    @AppId val appId: String,
    @ApplicationContext private val context: Context,
) : RumbleUseCase {

    suspend operator fun invoke(purchaseToken: String, videoId: Long?): SubscriptionResult {
        val result = subscriptionRepository.purchaseSubscription(
            appId = appId,
            productId = PremiumSubscription.SUBSCRIPTION_ID,
            purchaseToken = purchaseToken,
            appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: "",
            videoId = videoId,
        )
        if (result is SubscriptionResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}