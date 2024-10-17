package com.rumble.domain.login.domain.usecases

import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetClearSessionOnAppStartUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(): Boolean {
        return sessionManager.clearSessionOnAppStartFlow.first()
    }
}