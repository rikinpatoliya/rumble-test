package com.rumble.domain.billing.domain.usecase

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.QueryProductDetailsParams
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FetchRantProductDetailsUseCase @Inject constructor(
    private val connectToGooglePlayUseCase: ConnectToGooglePlayUseCase,
    private val billingClient: BillingClient,
    private val developModeUseCase: IsDevelopModeUseCase,
) {
    private val innerScope = CoroutineScope(Dispatchers.IO)

    suspend operator fun invoke(rantLevelList: List<RantLevel>) =
        suspendCoroutine<List<RantLevel>> { continuation ->
            if (developModeUseCase()) {
                continuation.resume(rantLevelList.filter { it.rantId.isNotEmpty() })
            } else {
                innerScope.launch {
                    if (connectToGooglePlayUseCase()) {
                        val queryProductDetailsParams =
                            QueryProductDetailsParams.newBuilder()
                                .setProductList(
                                    rantLevelList
                                        .filter { it.rantId.isNotEmpty() }
                                        .map {
                                            QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId(it.rantId)
                                                .setProductType(BillingClient.ProductType.INAPP)
                                                .build()
                                        }
                                ).build()

                        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK
                                && productDetailsList.isEmpty().not()
                            ) {
                                val result = rantLevelList.map {
                                    it.copy(productDetails = productDetailsList.find { productDetails -> productDetails.productId == it.rantId })
                                }.filter { it.productDetails != null }
                                continuation.resume(result)
                            } else {
                                continuation.resume(emptyList())
                            }
                        }
                    } else {
                        continuation.resume(rantLevelList.filter { it.rantId.isNotEmpty() })
                    }
                }
            }
        }
}