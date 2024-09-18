package com.rumble.domain.premium.domain.usecases

import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import javax.inject.Inject

class BuildPremiumSubscriptionParamsUseCase @Inject constructor() {
    operator fun invoke(productDetails: ProductDetails, offerToken: String): BillingFlowParams {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        return BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
    }
}