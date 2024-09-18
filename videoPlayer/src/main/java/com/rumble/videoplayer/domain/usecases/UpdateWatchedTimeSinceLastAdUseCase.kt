package com.rumble.videoplayer.domain.usecases

import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UpdateWatchedTimeSinceLastAdUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(duration: Float) {
        val currentValue = sessionManager.watchedTimeSinceLastAd.first()
        sessionManager.saveWatchedTimeSinceLastAd(currentValue + duration)
    }
}