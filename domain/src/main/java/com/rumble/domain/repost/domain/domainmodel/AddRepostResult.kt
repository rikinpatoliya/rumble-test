package com.rumble.domain.repost.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class AddRepostResult {
    data object Success: AddRepostResult()
    data class Failure(val error: RumbleError): AddRepostResult()
}