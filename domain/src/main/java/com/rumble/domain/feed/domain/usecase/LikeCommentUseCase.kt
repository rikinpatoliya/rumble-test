package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.domain.feed.domain.domainmodel.comments.CommentVoteResult
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.model.repository.FeedRepository
import javax.inject.Inject

class LikeCommentUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(commentEntity: CommentEntity): CommentVoteResult {
        val vote = if (commentEntity.userVote == UserVote.LIKE) UserVote.NONE else UserVote.LIKE
        return feedRepository.likeComment(commentEntity.commentId, vote).copy(userVote = vote)
    }
}