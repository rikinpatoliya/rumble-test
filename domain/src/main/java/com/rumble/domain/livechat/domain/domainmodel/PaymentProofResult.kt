package com.rumble.domain.livechat.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class PaymentProofResult {
    data object Success : PaymentProofResult()
    data class Failure(val rumbleError: RumbleError?, val errorMessage: String? = null) : PaymentProofResult()
}