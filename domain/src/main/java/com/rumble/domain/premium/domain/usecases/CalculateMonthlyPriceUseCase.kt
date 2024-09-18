package com.rumble.domain.premium.domain.usecases

import com.rumble.domain.premium.domain.domainmodel.SubscriptionType
import com.rumble.utils.extension.extractPrice
import com.rumble.utils.extension.round
import javax.inject.Inject

class CalculateMonthlyPriceUseCase @Inject constructor() {
    operator fun invoke(priceString: String, type: SubscriptionType): String? =
        if (type == SubscriptionType.Monthly) null
        else {
            try {
                val extractedPrice = priceString.extractPrice()
                val currency = priceString.substringBefore(extractedPrice)
                val monthlyPrice = (extractedPrice.replace(",", "").toFloat() / 12).round(2).toString()
                var firstPart = monthlyPrice.substringBefore(".")
                val secondPart = monthlyPrice.substringAfter(".")
                if (firstPart.length > 3) {
                    val commaIndex = firstPart.length - 3
                    firstPart = StringBuilder(firstPart).insert(commaIndex, ",").toString()
                    "$currency$firstPart.$secondPart"
                } else {
                    currency + monthlyPrice
                }
            } catch (e: Exception) {
                null
            }
        }
}