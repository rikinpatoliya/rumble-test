package com.rumble.domain.timerange.domain.usecases

import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.events.model.repository.EventRepository
import com.rumble.domain.timerange.model.repository.TimeRangeRepository
import com.rumble.videoplayer.player.TimeRangeData
import javax.inject.Inject

private const val TAG = "SaveTimeRangeUseCase"

class SaveTimeRangeUseCase @Inject constructor(
    private val timeRangeRepository: TimeRangeRepository,
    private val eventRepository: EventRepository,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
) {
    operator fun invoke(timeRangeData: TimeRangeData) {
        try {
            timeRangeRepository.saveTimeRange(timeRangeData)
            eventRepository.saveWatchProgress(timeRangeData)
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
        }
    }
}