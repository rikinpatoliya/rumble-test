package com.rumble.domain.premium.domain.domainmodel

import com.android.billingclient.api.ProductDetails

data class PremiumSubscriptionData(
    val type: SubscriptionType,
    val price: String,
    val monthlyPrice: String?,
    val productDetails: ProductDetails,
    val offerToken: String
)
