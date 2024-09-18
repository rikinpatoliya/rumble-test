package com.rumble.domain.billing.domain.usecase

import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import javax.inject.Inject

class BuildProductDetailsParamsUseCase @Inject constructor() {
    operator fun invoke(productDetails: ProductDetails): BillingFlowParams {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        return BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
    }
}