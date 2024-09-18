package com.rumble.battles.livechat.domain

import com.rumble.analytics.RantIAPSucceededEvent
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.livechat.domain.usecases.SendRantPurchasedEventUseCase
import com.rumble.network.session.SessionManager
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.math.BigDecimal

class SendRantPurchasedEventUseCaseTests {
    private val rantPrice = BigDecimal(9.99)
    private val creatorId = "creatorId"
    private val userId = "userId"
    private val analyticsEventUseCase: AnalyticsEventUseCase = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val sendRantPurchasedEventUseCase = SendRantPurchasedEventUseCase(analyticsEventUseCase, sessionManager)

    @Test
    fun testPriceCalculation() = runBlocking {
        sendRantPurchasedEventUseCase(rantPrice, creatorId)
        verify { analyticsEventUseCase.invoke(RantIAPSucceededEvent("9.99", "999.00", userId, creatorId)) }
    }
}