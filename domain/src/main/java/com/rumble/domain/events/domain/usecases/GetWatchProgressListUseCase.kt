package com.rumble.domain.events.domain.usecases

import com.rumble.domain.events.model.repository.EventRepository
import com.rumble.videoplayer.player.TimeRangeData
import javax.inject.Inject

class GetWatchProgressListUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(): List<TimeRangeData> =
        eventRepository.getTimeRangeList().distinct()
}