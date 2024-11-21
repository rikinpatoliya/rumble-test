package com.rumble.domain.repost.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.repost.domain.domainmodel.AddRepostResult
import com.rumble.domain.repost.model.repository.RepostRepository
import javax.inject.Inject

class AddRepostUseCase @Inject constructor(
    private val repostRepository: RepostRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(videoId: Long, channelId: Long, message: String): AddRepostResult {
        val result = repostRepository.addRepost(videoId, channelId, message)
        if (result is AddRepostResult.Failure) {
            rumbleErrorUseCase(result.error)
        }
        return result
    }
}