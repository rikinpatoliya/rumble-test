package com.rumble.domain.repost.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.repost.domain.domainmodel.DeleteRepostResult
import com.rumble.domain.repost.model.repository.RepostRepository
import javax.inject.Inject

class DeleteRepostUseCase @Inject constructor(
    private val repostRepository: RepostRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(repostId: Long): DeleteRepostResult {
        val result = repostRepository.deleteRepost(repostId)
        if (result is DeleteRepostResult.Failure) {
            rumbleErrorUseCase(result.error)
        }
        return result
    }
}