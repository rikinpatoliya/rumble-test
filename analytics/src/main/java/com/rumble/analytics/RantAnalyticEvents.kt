package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import java.math.BigDecimal

data class RantBuyButtonTapEvent(private val price: BigDecimal) : AnalyticEvent {
    override val eventName: String = "PaidRant_BuyButton_Tap"
    override val firebaseOps: Bundle = bundleOf(PRICE to price)
    override val appsFlyOps: Map<String, String> = mapOf(Pair(PRICE, price.toString()))
}

object RantCloseButtonTapEvent : AnalyticEvent {
    override val eventName: String = "PaidRant_CloseButton_Tap"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

object RantTermsLinkTapEvent : AnalyticEvent {
    override val eventName: String = "PaidRant_TermsLink_Tap"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = mapOf()
}

data class RantIAPSucceededEvent(private val price: Double, private val priceCents: String, private val userId: String, private val creatorId: String) : AnalyticEvent {
    override val eventName: String = "PaidRant_IapSucceeded"
    override val firebaseOps: Bundle = bundleOf(FIREBASE_PRICE to price, FIREBASE_CURRENCY to DEFAULT_CURRENCY, PRICE to priceCents, USER_ID to userId, CREATOR_ID to creatorId)
    override val appsFlyOps: Map<String, String> = mapOf(Pair(APPSFLYER_PRICE, price.toString()), Pair(APPSFLYER_CURRENCY, DEFAULT_CURRENCY), Pair(PRICE, priceCents), Pair(USER_ID, userId), Pair(CREATOR_ID, creatorId))
}
