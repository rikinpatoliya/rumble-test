package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.rumble.network.queryHelpers.SubscriptionSource

data class PremiumIAPSucceededEvent(
    private val price: Double,
    private val priceCents: String,
    private val userId: String,
    private val contentId: String?,
    private val creatorId: String?,
    private val source: SubscriptionSource?,
) : AnalyticEvent {
    override val eventName: String = "Premium_IapSucceeded"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_PRICE to price,
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId,
        CONTENT_ID to contentId,
        SOURCE to (source?.value ?: ""),
        CREATOR_ID to creatorId,
    )
    override val appsFlyOps: Map<String, String?> =  mapOf(
        APPSFLYER_PRICE to price.toString(),
        APPSFLYER_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId,
        CONTENT_ID to contentId,
        SOURCE to (source?.value ?: ""),
        CREATOR_ID to creatorId,
    )
}

data class PremiumMonthlyIAPSucceededEvent(
    private val price: Double,
    private val priceCents: String,
    private val userId: String,
    private val contentId: String?,
    private val creatorId: String?,
    private val source: SubscriptionSource?,
) : AnalyticEvent {
    override val eventName: String = "PremiumMonthly_IapSucceeded"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_PRICE to price,
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId,
        CONTENT_ID to contentId,
        SOURCE to (source?.value ?: ""),
    )
    override val appsFlyOps: Map<String, String?> =  mapOf(
        APPSFLYER_PRICE to price.toString(),
        APPSFLYER_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId,
        CONTENT_ID to contentId,
        SOURCE to (source?.value ?: ""),
        CREATOR_ID to creatorId
    )
}

data class PremiumAnnualIAPSucceededEvent(
    private val price: Double,
    private val priceCents: String,
    private val userId: String,
    private val contentId: String?,
    private val creatorId: String?,
    private val source: SubscriptionSource?,
) : AnalyticEvent {
    override val eventName: String = "PremiumAnnual_IapSucceeded"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_PRICE to price,
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId,
        CONTENT_ID to contentId,
        SOURCE to (source?.value ?: ""),
    )
    override val appsFlyOps: Map<String, String?> =  mapOf(
        APPSFLYER_PRICE to price.toString(),
        APPSFLYER_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId,
        CONTENT_ID to contentId,
        SOURCE to (source?.value ?: ""),
        CREATOR_ID to creatorId
    )
}

data object PremiumPromoViewEvent : AnalyticEvent {
    override val eventName: String = "PremiumPromo_View"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

data object PremiumPromoCloseEvent : AnalyticEvent {
    override val eventName: String = "PremiumPromo_Close"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

data object PremiumPromoGetButtonTapEvent : AnalyticEvent {
    override val eventName: String = "PremiumPromo_GetButton_Tap"
    override val firebaseOps: Bundle = bundleOf()
    override val appsFlyOps: Map<String, String> = emptyMap()
}

