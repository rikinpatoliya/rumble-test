package com.rumble.domain.feed.domain.domainmodel.comments

data class UserComment(
    val comment: String,
    val videoId: Long,
    val commentId: Long? = null,
)
