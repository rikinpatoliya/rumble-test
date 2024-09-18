package com.rumble.domain.events.domain.usecases

import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProvideDebugUserIdUseCase @Inject constructor(
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(): String =
        sessionManager.userIdFlow.first().ifEmpty { "anonymous" }
}