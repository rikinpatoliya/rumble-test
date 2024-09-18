package com.rumble.domain.premium.domain.usecases

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.rumble.domain.billing.domain.usecase.ConnectToGooglePlayUseCase
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscription
import com.rumble.domain.premium.domain.domainmodel.PremiumSubscriptionData
import com.rumble.domain.premium.domain.domainmodel.SubscriptionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FetchPremiumSubscriptionListUseCase @Inject constructor(
    private val connectToGooglePlayUseCase: ConnectToGooglePlayUseCase,
    private val calculateMonthlyPriceUseCase: CalculateMonthlyPriceUseCase,
    private val billingClient: BillingClient
) {
    private val innerScope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke() = suspendCoroutine { continuation ->
        innerScope.launch {
            if (connectToGooglePlayUseCase()) {
                val queryProductDetailsParams =
                    QueryProductDetailsParams.newBuilder()
                        .setProductList(
                            listOf(QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(PremiumSubscription.SUBSCRIPTION_ID)
                                .setProductType(BillingClient.ProductType.SUBS)
                                .build())
                        ).build()

                billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                        && productDetailsList.isEmpty().not()
                    ) {
                        val result = getSubscriptionDataList(productDetailsList)?.sortedBy { it.type }
                        continuation.resume(result)
                    } else {
                        continuation.resume(null)
                    }
                }
            } else {
                continuation.resume(null)
            }
        }
    }

    private fun getSubscriptionDataList(productDetailsList: List<ProductDetails>): List<PremiumSubscriptionData>? =
        productDetailsList.firstOrNull()?.let { productDetails ->
            return productDetails.subscriptionOfferDetails?.map { subscriptionOfferDetails ->
                val type = SubscriptionType.findByProductId(subscriptionOfferDetails.basePlanId)
                    ?: SubscriptionType.Monthly
                val priceString = subscriptionOfferDetails.pricingPhases.pricingPhaseList.firstOrNull()?.formattedPrice
                    ?: ""
                PremiumSubscriptionData(
                    type = type,
                    price = priceString,
                    offerToken = subscriptionOfferDetails.offerToken,
                    productDetails = productDetails,
                    monthlyPrice = calculateMonthlyPriceUseCase(priceString, type)
                )
            }
        }
}