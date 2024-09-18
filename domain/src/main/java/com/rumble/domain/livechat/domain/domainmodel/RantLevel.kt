package com.rumble.domain.livechat.domain.domainmodel

import com.android.billingclient.api.ProductDetails
import java.math.BigDecimal

data class RantLevel(
    val rantPrice: BigDecimal,
    val duration: Int,
    val foregroundColor: String,
    val backgroundColor: String,
    val mainColor: String,
    val rantId: String,
    val productDetails: ProductDetails? = null
)