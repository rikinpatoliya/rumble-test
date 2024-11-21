package com.rumble.domain.repost.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class DeleteRepostResult {
    data object Success: DeleteRepostResult()
    data class Failure(val error: RumbleError): DeleteRepostResult()
}