package com.rumble.domain.livechat.domain.usecases

import com.rumble.analytics.SubsGiftsIAPSucceededEvent
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.network.session.SessionManager
import com.rumble.utils.extension.toPriceString
import kotlinx.coroutines.flow.first
import java.math.BigDecimal
import javax.inject.Inject

class SendGiftSubsPurchasedEventUseCase @Inject constructor(
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(giftPrice: BigDecimal, creatorId: String) {
        val userId = sessionManager.userIdFlow.first()
        analyticsEventUseCase(
            event = SubsGiftsIAPSucceededEvent(
                giftPrice.toDouble(),
                giftPrice.multiply(BigDecimal(100)).toPriceString(),
                userId,
                creatorId
            ),
            sendDebugLog = true
        )
    }
}