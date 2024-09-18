package com.rumble.domain.video.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.domain.domainmodel.EmptyResult
import com.rumble.domain.video.model.repository.VideoRepository
import javax.inject.Inject

class RequestEmailVerificationUseCase @Inject constructor(
    override val rumbleErrorUseCase: RumbleErrorUseCase,
    private val videoRepository: VideoRepository,
) : RumbleUseCase {
    suspend operator fun invoke(email: String): EmptyResult {
        val result = videoRepository.requestVerificationEmail(email)
        if (result is EmptyResult.Failure) {
            rumbleErrorUseCase.invoke(result.rumbleError)
        }

        return result
    }
}