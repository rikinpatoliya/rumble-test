package com.rumble.domain.landing.usecases

import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUserCookiesUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(): String = sessionManager.cookiesFlow.first()
}