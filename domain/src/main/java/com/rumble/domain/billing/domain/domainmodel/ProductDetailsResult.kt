package com.rumble.domain.billing.domain.domainmodel

import com.android.billingclient.api.ProductDetails

sealed class ProductDetailsResult {
    data class Success(val productDetails: ProductDetails): ProductDetailsResult()
    object Error: ProductDetailsResult()
}
