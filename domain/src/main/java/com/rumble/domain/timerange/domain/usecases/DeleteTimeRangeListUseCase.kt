package com.rumble.domain.timerange.domain.usecases

import com.rumble.domain.timerange.model.repository.TimeRangeRepository
import javax.inject.Inject

class DeleteTimeRangeListUseCase @Inject constructor(
    private val timeRangeRepository: TimeRangeRepository
) {
    suspend operator fun invoke() = timeRangeRepository.clearReportedTimeRanges()
}