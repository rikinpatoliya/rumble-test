package com.rumble.domain.billing.domain.usecase

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.QueryProductDetailsParams
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftDetails
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FetchGiftProductDetailsUseCase @Inject constructor(
    private val connectToGooglePlayUseCase: ConnectToGooglePlayUseCase,
    private val billingClient: BillingClient,
    private val developModeUseCase: IsDevelopModeUseCase,
) {
    private val innerScope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(premiumGiftEntity: PremiumGiftEntity) =
        suspendCoroutine<List<PremiumGiftDetails>> { continuation ->
            if (developModeUseCase()) {
                continuation.resume(premiumGiftEntity.giftList.filter { it.productId.isNotEmpty() })
            } else {
                innerScope.launch {
                    if (connectToGooglePlayUseCase()) {
                        val queryProductDetailsParams =
                            QueryProductDetailsParams.newBuilder()
                                .setProductList(
                                    premiumGiftEntity.giftList
                                        .filter { it.productId.isNotEmpty() }
                                        .map {
                                            QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(it.productId)
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()
                                        }
                                ).build()

                        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                                && productDetailsList.isEmpty().not()
                            ) {
                                val result = premiumGiftEntity.giftList.map {
                                    it.copy(productDetails = productDetailsList.find { productDetails -> productDetails.productId == it.productId })
                                }.filter { it.productDetails != null }
                                continuation.resume(result)
                            } else {
                                continuation.resume(emptyList())
                            }
                        }
                    } else {
                        continuation.resume(premiumGiftEntity.giftList.filter { it.productId.isNotEmpty() })
                    }
                }
            }
        }
}