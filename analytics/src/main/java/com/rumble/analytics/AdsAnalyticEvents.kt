package com.rumble.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import java.math.BigDecimal

data class AdsCommonEvent(private val price: BigDecimal?, private val userId: String) : AnalyticEvent {
    override val eventName: String = "ad_impression"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        FIREBASE_PRICE to (price?.toDouble() ?: DEFAULT_PRICE),
        USER_ID to userId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        APPSFLYER_CURRENCY to DEFAULT_CURRENCY,
        APPSFLYER_PRICE to (price?.toString() ?: DEFAULT_PRICE.toString()),
        USER_ID to userId
    )
}

data class RumbleUpNextImpressionEvent(private val price: BigDecimal?, private val userId: String, private val creatorId: String) : AnalyticEvent {
    override val eventName: String = "ad_impression_upnext_rumble"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        FIREBASE_PRICE to (price?.toDouble() ?: DEFAULT_PRICE),
        USER_ID to userId,
        CREATOR_ID to creatorId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        APPSFLYER_CURRENCY to DEFAULT_CURRENCY,
        APPSFLYER_PRICE to (price?.toString() ?: DEFAULT_PRICE.toString()),
        USER_ID to userId,
        CREATOR_ID to creatorId
    )
}

data class RumbleAdFeedImpressionEvent(private val price: BigDecimal?, private val userId: String) : AnalyticEvent {
    override val eventName: String = "ad_impression_feed_rumble"
    override val firebaseOps: Bundle = bundleOf(
        FIREBASE_CURRENCY to DEFAULT_CURRENCY,
        FIREBASE_PRICE to (price?.toDouble() ?: DEFAULT_PRICE),
        USER_ID to userId
    )
    override val appsFlyOps: Map<String, String> = mapOf(
        APPSFLYER_CURRENCY to DEFAULT_CURRENCY,
        APPSFLYER_PRICE to (price?.toString() ?: DEFAULT_PRICE.toString()),
        USER_ID to userId
    )
}