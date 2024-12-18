package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf

data class PremiumGiftsIAPSucceededEvent(
    private val price: Double,
    private val priceCents: String,
    private val userId: String,
    private val creatorId: String
) : AnalyticEvent {
    override val eventName: String = "PremiumGifts_IapSucceeded"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_PRICE to price,
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId,
        CREATOR_ID to creatorId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        Pair(APPSFLYER_PRICE, price.toString()),
        Pair(APPSFLYER_CURRENCY, DEFAULT_CURRENCY),
        Pair(PRICE, priceCents),
        Pair(USER_ID, userId),
        Pair(CREATOR_ID, creatorId)
    )
}

data class SubsGiftsIAPSucceededEvent(
    private val price: Double,
    private val priceCents: String,
    private val userId: String,
    private val creatorId: String
) : AnalyticEvent {
    override val eventName: String = "SubsGifts_IapSucceeded"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_PRICE to price,
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        PRICE to priceCents,
        USER_ID to userId,
        CREATOR_ID to creatorId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        Pair(APPSFLYER_PRICE, price.toString()),
        Pair(APPSFLYER_CURRENCY, DEFAULT_CURRENCY),
        Pair(PRICE, priceCents),
        Pair(USER_ID, userId),
        Pair(CREATOR_ID, creatorId)
    )
}