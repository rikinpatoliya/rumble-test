package com.rumble.domain.feed.domain.domainmodel.comments

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.video.UserVote

sealed class CommentVoteResult {
    data class Success(val commentId: Long, val userVote: UserVote) : CommentVoteResult()
    data class Failure(val rumbleError: RumbleError, val errorMessage: String?) : CommentVoteResult()
}
