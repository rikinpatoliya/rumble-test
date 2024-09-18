package com.rumble.domain.billing.domain.usecase

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.ConnectionState
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ConnectToGooglePlayUseCase @Inject constructor(
    private val billingClient: BillingClient
) {

    suspend operator fun invoke() = suspendCoroutine { continuation ->
        if (billingClient.connectionState == ConnectionState.CONNECTED) {
            continuation.resume(true)
        } else {
            val stateListener = object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        continuation.resume(true)
                    } else {
                        continuation.resume(false)
                    }
                }

                override fun onBillingServiceDisconnected() {}
            }
            billingClient.startConnection(stateListener)
        }
    }
}