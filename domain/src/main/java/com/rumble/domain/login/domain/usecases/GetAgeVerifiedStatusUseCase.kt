package com.rumble.domain.login.domain.usecases

import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAgeVerifiedStatusUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(): Boolean? {
        return sessionManager.ageVerifiedFlow.first()
    }
}