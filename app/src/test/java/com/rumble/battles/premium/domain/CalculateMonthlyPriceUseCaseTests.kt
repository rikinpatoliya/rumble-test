package com.rumble.battles.premium.domain

import com.rumble.domain.premium.domain.domainmodel.SubscriptionType
import com.rumble.domain.premium.domain.usecases.CalculateMonthlyPriceUseCase
import org.junit.Test

class CalculateMonthlyPriceUseCaseTests {

    private val calculateMonthlyPriceUseCase: CalculateMonthlyPriceUseCase = CalculateMonthlyPriceUseCase()

    @Test
    fun testPriceCalculation() {
        var price = "109,000"
        var expected = "9,083.33"
        assert(expected == calculateMonthlyPriceUseCase(price, SubscriptionType.Annually))

        price = "99"
        expected = "8.25"
        assert(expected == calculateMonthlyPriceUseCase(price, SubscriptionType.Annually))
    }
}