package com.rumble.domain.premium.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class SubscriptionResult {
    object Success: SubscriptionResult()
    data class PurchaseFailure(val errorMessage: String?): SubscriptionResult()
    data class Failure(val rumbleError: RumbleError): SubscriptionResult()
}