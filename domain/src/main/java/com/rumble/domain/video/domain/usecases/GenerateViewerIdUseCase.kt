package com.rumble.domain.video.domain.usecases

import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants.VIEWER_CHAR_POOL
import com.rumble.utils.RumbleConstants.VIEWER_ID_LENGTH
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GenerateViewerIdUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke() {
        if (sessionManager.viewerIdFlow.first().isEmpty()) {
            sessionManager.saveViewerId(
                List(VIEWER_ID_LENGTH) { VIEWER_CHAR_POOL.random() }.joinToString("")
            )
        }
    }
}