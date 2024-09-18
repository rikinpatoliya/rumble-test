package com.rumble.domain.feed.domain.domainmodel.comments

import android.os.Parcelable
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class CommentEntity(
    val commentId: Long,
    val author: String,
    val authorId: String,
    val authorThumb: String,
    val commentText: String,
    val replayList: List<CommentEntity>?,
    val date: LocalDateTime?,
    val userVote: UserVote,
    val likeNumber: Int,
    val currentUserComment: Boolean = false,
    val repliedByCurrentUser: Boolean = false,
    val displayReplies: Boolean = false,
    val verifiedBadge: Boolean,
): Parcelable
