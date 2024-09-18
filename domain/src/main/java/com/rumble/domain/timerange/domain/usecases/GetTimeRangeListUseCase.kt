package com.rumble.domain.timerange.domain.usecases

import com.rumble.domain.timerange.model.repository.TimeRangeRepository
import com.rumble.videoplayer.player.TimeRangeData
import javax.inject.Inject

class GetTimeRangeListUseCase @Inject constructor(
    private val timeRangeRepository: TimeRangeRepository
) {
    suspend operator fun invoke(): List<TimeRangeData> =
        timeRangeRepository.getTimeRangeList().distinct()
}