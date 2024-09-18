package com.rumble.domain.feed.domain.domainmodel.comments

import com.rumble.domain.feed.domain.domainmodel.video.UserVote

data class CommentVoteResult(
    val success: Boolean,
    val commentId: Long,
    val userVote: UserVote
)
