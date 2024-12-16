package com.rumble.domain.premium.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class PurchaseResult {
    data object Success: PurchaseResult()
    data class PurchaseFailure(val errorMessage: String?): PurchaseResult()
    data class Failure(val rumbleError: RumbleError): PurchaseResult()
}