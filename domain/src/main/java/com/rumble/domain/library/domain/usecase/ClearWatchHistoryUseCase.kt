package com.rumble.domain.library.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.library.domain.model.ClearWatchHistoryResult
import com.rumble.domain.library.model.repository.PlayListRepository
import javax.inject.Inject

class ClearWatchHistoryUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(): ClearWatchHistoryResult {
        val result = playListRepository.clearWatchHistory()
        if (result is ClearWatchHistoryResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}