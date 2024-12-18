package com.rumble.domain.livechat.domain.usecases

import com.rumble.analytics.SubsGiftsIAPSucceededEvent
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SendGiftSubsPurchasedEventUseCase @Inject constructor(
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(giftPrice: Int, creatorId: String) {
        val userId = sessionManager.userIdFlow.first()
        analyticsEventUseCase(
            event = SubsGiftsIAPSucceededEvent(
                giftPrice.toDouble(),
                giftPrice.toString(),
                userId,
                creatorId
            ),
            sendDebugLog = true
        )
    }
}