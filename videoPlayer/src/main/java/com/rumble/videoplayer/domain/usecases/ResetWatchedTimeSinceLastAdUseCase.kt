package com.rumble.videoplayer.domain.usecases

import com.rumble.network.session.SessionManager
import javax.inject.Inject

class ResetWatchedTimeSinceLastAdUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke() {
        sessionManager.saveWatchedTimeSinceLastAd(0f)
    }
}