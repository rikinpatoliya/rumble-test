package com.rumble.domain.landing.usecases

import com.rumble.domain.login.domain.usecases.RumbleLoginUseCase
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SilentLoginUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val loginUseCase: RumbleLoginUseCase
) {
    suspend operator fun invoke(): Boolean {
        val userName = sessionManager.userNameFlow.first()
        val password = sessionManager.passwordFlow.first()
        return if (userName.isNotEmpty() and password.isNotEmpty())
            loginUseCase(userName, password).success
        else false
    }
}