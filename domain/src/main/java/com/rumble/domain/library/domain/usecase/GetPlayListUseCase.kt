package com.rumble.domain.library.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.domain.domainmodel.PlayListResult
import com.rumble.domain.library.model.repository.PlayListRepository
import javax.inject.Inject

class GetPlayListUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(playListId: String): PlayListResult {
        val result = playListRepository.fetchPlayList(playListId)
        if (result is PlayListResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}