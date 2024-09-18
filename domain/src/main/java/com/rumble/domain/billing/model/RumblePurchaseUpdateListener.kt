package com.rumble.domain.billing.model

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener

interface PurchaseHandler {
    fun onPurchaseFinished(result: PurchaseResult)
}

sealed class PurchaseResult {
    data class Success(val purchaseToken: String) : PurchaseResult()
    data class Failure(val errorMessage: String, val code: Int) : PurchaseResult()
}

class RumblePurchaseUpdateListener : PurchasesUpdatedListener {
    private val handlers = mutableListOf<PurchaseHandler>()

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        val token =  purchases?.first()?.purchaseToken
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK &&  token != null) {
            handlers.forEach { it.onPurchaseFinished(PurchaseResult.Success(token)) }
        } else {
            handlers.forEach { it.onPurchaseFinished(PurchaseResult.Failure(mapBillingResultError(billingResult.responseCode), billingResult.responseCode)) }
        }
    }

    fun subscribeToPurchaseUpdate(handler: PurchaseHandler) {
        handlers.add(handler)
    }

    fun unsubscribeFromPurchaseUpdate(handler: PurchaseHandler) {
        handlers.remove(handler)
    }
}

private fun mapBillingResultError(code: Int) =
    when(code) {
        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED ->
            "User already owns the item being purchased"
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE ->
            "Item is not available to be purchased"
        BillingClient.BillingResponseCode.USER_CANCELED ->
            "User dismissed the purchase flow"
        else -> "Unknown billing error"
    }