package com.rumble.domain.channels.channeldetails.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class CommentAuthorsResult {
    data class Success(val authors: List<CommentAuthorEntity>) : CommentAuthorsResult()
    data class Failure(val rumbleError: RumbleError?) : CommentAuthorsResult()
}
