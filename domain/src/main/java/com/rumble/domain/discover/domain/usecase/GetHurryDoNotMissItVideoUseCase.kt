package com.rumble.domain.discover.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.common.domain.domainmodel.VideoResult
import com.rumble.domain.discover.model.repository.DiscoverRepository
import com.rumble.utils.RumbleConstants.LIMIT_TO_ONE
import javax.inject.Inject

class GetHurryDoNotMissItVideoUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke() =
        when (val result = discoverRepository.getEditorsPicks(offset = 0, limit = LIMIT_TO_ONE)) {
            is VideoListResult.Failure -> {
                rumbleErrorUseCase(result.rumbleError)
                VideoResult.Failure(result.rumbleError)
            }

            is VideoListResult.Success -> {
                VideoResult.Success(result.videoList.first())
            }
        }
}