package com.rumble.domain.landing.usecases

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

class LoginRequiredUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(): Boolean {
        val promptPeriod = FirebaseRemoteConfig.getInstance().getLong("auth_display_delay_days")
        val cookies = sessionManager.cookiesFlow.first()
        val lastLoginPromptTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(sessionManager.lastLoginPromptTimeFlow.first()), ZoneId.of("UTC"))
        val daysFromLastPrompt = Duration.between(lastLoginPromptTime, LocalDateTime.now().atOffset(ZoneOffset.UTC)).toDays()
        val required = cookies.isBlank() and (daysFromLastPrompt > promptPeriod)
        if (required) {
            sessionManager.saveLastLoginPromptTime(
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            )
        }
        return required
    }
}