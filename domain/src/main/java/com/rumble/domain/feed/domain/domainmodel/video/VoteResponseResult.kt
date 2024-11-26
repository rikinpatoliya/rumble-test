package com.rumble.domain.feed.domain.domainmodel.video

import com.rumble.domain.common.model.RumbleError

sealed class VoteResponseResult {
    data object Success : VoteResponseResult()
    data class Failure(val rumbleError: RumbleError, val errorMessage: String) : VoteResponseResult()
}