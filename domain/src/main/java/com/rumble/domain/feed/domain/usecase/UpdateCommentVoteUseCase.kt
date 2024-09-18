package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import javax.inject.Inject
import kotlin.math.max

class UpdateCommentVoteUseCase @Inject constructor() {

    operator fun invoke(
        commentId: Long,
        userVote: UserVote,
        comments: List<CommentEntity>
    ): List<CommentEntity> =
        comments.map {
            updateVote(commentId, userVote, it).copy(
                replayList = invoke(commentId, userVote, it.replayList ?: emptyList())
            )
        }

    private fun updateVote(
        commentId: Long,
        userVote: UserVote,
        commentEntity: CommentEntity
    ): CommentEntity =
        commentEntity.copy(
            userVote = if (commentEntity.commentId == commentId) userVote else commentEntity.userVote,
            replayList = commentEntity.replayList?.map { updateVote(commentId, userVote, it) },
            likeNumber = getLikesNumber(commentId, userVote, commentEntity)
        )

    private fun getLikesNumber(
        commentId: Long,
        userVote: UserVote,
        commentEntity: CommentEntity
    ): Int =
        if (commentEntity.commentId == commentId && userVote == UserVote.LIKE) commentEntity.likeNumber + 1
        else if (commentEntity.commentId == commentId) max(commentEntity.likeNumber - 1, 0)
        else commentEntity.likeNumber
}