package com.rumble.domain.discover.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.discover.model.repository.DiscoverRepository
import javax.inject.Inject

class GetLiveVideoListUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke() =
        when (val result = discoverRepository.getLiveNowShortList()) {
            is VideoListResult.Failure -> {
                rumbleErrorUseCase(result.rumbleError)
                result
            }
            is VideoListResult.Success -> result
        }
}