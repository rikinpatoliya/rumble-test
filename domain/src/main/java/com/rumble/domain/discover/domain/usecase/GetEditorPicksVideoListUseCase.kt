package com.rumble.domain.discover.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.discover.model.repository.DiscoverRepository
import com.rumble.utils.RumbleConstants.LIMIT_TO_THREE
import javax.inject.Inject
import kotlin.math.min

class GetEditorPicksVideoListUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke() =
        when (val result = discoverRepository.getEditorsPicks(offset = 1, limit = LIMIT_TO_THREE)) {
            is VideoListResult.Failure -> {
                rumbleErrorUseCase(result.rumbleError)
                result
            }
            is VideoListResult.Success -> {
                result.copy(
                    videoList = result.videoList.subList(
                        0,
                        min(result.videoList.size, LIMIT_TO_THREE)
                    ).mapIndexed { index, videoEntity ->
                        videoEntity.copy(index = index)
                    }
                )
            }
        }
}