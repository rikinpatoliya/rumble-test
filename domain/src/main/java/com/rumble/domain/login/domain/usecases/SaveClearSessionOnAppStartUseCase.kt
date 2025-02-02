package com.rumble.domain.login.domain.usecases

import com.rumble.network.session.SessionManager
import javax.inject.Inject

class SaveClearSessionOnAppStartUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(verified: Boolean) {
        sessionManager.saveClearSessionOnAppStart(verified)
    }
}