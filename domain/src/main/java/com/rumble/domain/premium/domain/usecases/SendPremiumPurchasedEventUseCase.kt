package com.rumble.domain.premium.domain.usecases

import com.rumble.analytics.PremiumAnnualIAPSucceededEvent
import com.rumble.analytics.PremiumIAPSucceededEvent
import com.rumble.analytics.PremiumMonthlyIAPSucceededEvent
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscription
import com.rumble.domain.premium.domain.domainmodel.SubscriptionType
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SendPremiumPurchasedEventUseCase @Inject constructor(
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(subscriptionType: SubscriptionType) {
        val userId = sessionManager.userIdFlow.first()
        if (subscriptionType == SubscriptionType.Annually) {
            analyticsEventUseCase(PremiumIAPSucceededEvent(PremiumSubscription.ANNUAL_PRICE_DOLLAR, PremiumSubscription.ANNUAL_PRICE_CENT, userId), true)
            analyticsEventUseCase(PremiumAnnualIAPSucceededEvent(PremiumSubscription.ANNUAL_PRICE_DOLLAR, PremiumSubscription.ANNUAL_PRICE_CENT, userId), true)
        }
        else if (subscriptionType == SubscriptionType.Monthly){
            analyticsEventUseCase(PremiumIAPSucceededEvent(PremiumSubscription.MONTHLY_PRICE_DOLLAR, PremiumSubscription.MONTHLY_PRICE_CENT, userId), true)
            analyticsEventUseCase(PremiumMonthlyIAPSucceededEvent(PremiumSubscription.MONTHLY_PRICE_DOLLAR, PremiumSubscription.MONTHLY_PRICE_CENT, userId), true)
        }
    }
}