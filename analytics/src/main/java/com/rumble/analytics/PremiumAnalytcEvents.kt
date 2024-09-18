package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf

data class PremiumIAPSucceededEvent(private val price: Double, private val priceCents: String, private val userId: String) : AnalyticEvent {
    override val eventName: String = "Premium_IapSucceeded"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_PRICE to price,
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        APPSFLYER_PRICE to price.toString(),
        APPSFLYER_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId
    )
}

data class PremiumMonthlyIAPSucceededEvent(private val price: Double, private val priceCents: String, private val userId: String) : AnalyticEvent {
    override val eventName: String = "PremiumMonthly_IapSucceeded"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_PRICE to price,
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        APPSFLYER_PRICE to price.toString(),
        APPSFLYER_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId
    )
}

data class PremiumAnnualIAPSucceededEvent(private val price: Double, private val priceCents: String, private val userId: String) : AnalyticEvent {
    override val eventName: String = "PremiumAnnual_IapSucceeded"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_PRICE to price,
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        APPSFLYER_PRICE to price.toString(),
        APPSFLYER_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId
    )
}

object PremiumPromoViewEvent : AnalyticEvent {
    override val eventName: String = "PremiumPromo_View"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}
object PremiumPromoCloseEvent : AnalyticEvent {
    override val eventName: String = "PremiumPromo_Close"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}
object PremiumPromoGetButtonTapEvent : AnalyticEvent {
    override val eventName: String = "PremiumPromo_GetButton_Tap"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

