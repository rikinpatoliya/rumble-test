package com.rumble.domain.feed.domain.domainmodel.comments

data class CommentResult(
    val success: Boolean = false,
    val commentId: Long? = null,
    val tooShort: Boolean = false,
    val error: String? = null
)