package com.rumble.domain.events.domain.usecases

import com.rumble.domain.events.model.repository.EventRepository
import javax.inject.Inject

class DeleteReportedWatchProgressUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke() = eventRepository.clearWatchProgress()
}