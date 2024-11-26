package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VoteResponseResult
import com.rumble.domain.feed.domain.domainmodel.video.VoteResult
import com.rumble.domain.feed.model.repository.FeedRepository
import java.lang.Long.max
import javax.inject.Inject

class VoteVideoUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(videoEntity: VideoEntity, userVote: UserVote): VoteResult {
        val result = if (userVote == UserVote.LIKE) {
            likeVideo(videoEntity)
        } else {
            dislikeVideo(videoEntity)
        }
        if (result.success.not()) rumbleErrorUseCase(result.rumbleError)
        return result
    }

    private suspend fun likeVideo(videoEntity: VideoEntity): VoteResult {
        val userVote = if (videoEntity.userVote == UserVote.LIKE) UserVote.NONE else UserVote.LIKE
        val newDislikeNumber =
            if (videoEntity.userVote == UserVote.DISLIKE) decreaseCounter(videoEntity.dislikeNumber) else videoEntity.dislikeNumber
        val voteResult: VoteResponseResult = feedRepository.voteVideo(
            videoEntityId = videoEntity.id,
            userVote = userVote
        )
        return mapVoteResult(
            voteResponseResult = voteResult,
            videoEntity = videoEntity,
            userVote = userVote,
            likeNumber = increaseCounter(userVote, videoEntity.likeNumber),
            dislikeNumber = newDislikeNumber,
        )
    }

    private suspend fun dislikeVideo(videoEntity: VideoEntity): VoteResult {
        val userVote =
            if (videoEntity.userVote == UserVote.DISLIKE) UserVote.NONE else UserVote.DISLIKE
        val newLikeNumber =
            if (videoEntity.userVote == UserVote.LIKE) decreaseCounter(videoEntity.likeNumber) else videoEntity.likeNumber
        val voteResult: VoteResponseResult = feedRepository.voteVideo(
            videoEntityId = videoEntity.id,
            userVote = userVote
        )
        return mapVoteResult(
            voteResponseResult = voteResult,
            videoEntity = videoEntity,
            userVote = userVote,
            likeNumber = newLikeNumber,
            dislikeNumber = increaseCounter(userVote, videoEntity.dislikeNumber),
        )
    }

    private fun increaseCounter(vote: UserVote, number: Long): Long {
        return when (vote) {
            UserVote.NONE -> if (number > 0) number.minus(1) else 0
            else -> number.plus(1)
        }
    }

    private fun decreaseCounter(number: Long): Long = max(0, number.minus(1))

    private fun mapVoteResult(
        voteResponseResult: VoteResponseResult,
        videoEntity: VideoEntity,
        userVote: UserVote,
        likeNumber: Long,
        dislikeNumber: Long,
    ) = when (voteResponseResult) {
        is VoteResponseResult.Success -> {
            VoteResult(
                success = true,
                updatedFeed = videoEntity.copy(
                    likeNumber = likeNumber,
                    dislikeNumber = dislikeNumber,
                    userVote = userVote,
                ),
            )
        }

        is VoteResponseResult.Failure -> {
            VoteResult(
                success = false,
                updatedFeed = videoEntity.copy(
                    likeNumber = likeNumber,
                    dislikeNumber = dislikeNumber,
                    userVote = userVote,
                ),
                rumbleError = voteResponseResult.rumbleError,
                errorMessage = voteResponseResult.errorMessage,
            )
        }
    }
}