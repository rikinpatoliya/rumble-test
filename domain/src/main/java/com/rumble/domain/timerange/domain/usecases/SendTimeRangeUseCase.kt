package com.rumble.domain.timerange.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.timerange.domain.domainmodel.SendTimeRangeListResult
import com.rumble.domain.timerange.model.repository.TimeRangeRepository
import com.rumble.videoplayer.player.TimeRangeData
import javax.inject.Inject

private const val TAG = "SendTimeRangeUseCase"

class SendTimeRangeUseCase @Inject constructor(
    private val timeRangeRepository: TimeRangeRepository,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase
): RumbleUseCase {
    suspend operator fun invoke(timeRangeList: List<TimeRangeData>, endpoint: String) {
        try {
            val result = timeRangeRepository.sendTimeRangeList(timeRangeList, endpoint)
            if (result is SendTimeRangeListResult.Failure) {
                rumbleErrorUseCase(result.rumbleError)
            }
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
        }
    }
}