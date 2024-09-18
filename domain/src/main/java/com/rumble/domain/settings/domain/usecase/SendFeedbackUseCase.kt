package com.rumble.domain.settings.domain.usecase

import android.os.Build
import com.rumble.domain.common.domain.usecase.SendEmailUseCase
import com.rumble.network.di.AppVersion
import com.rumble.network.di.OsVersion
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants.SUPPORT_EMAIL
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SendFeedbackUseCase @Inject constructor(
    private val sendEmailUseCase: SendEmailUseCase,
    private val sessionManager: SessionManager,
    @AppVersion private val appVersion: String,
    @OsVersion private val osVersion: String,
) {
    suspend operator fun invoke() {
        val userName = sessionManager.userNameFlow.first()
        val subject = "Rumble Android App $appVersion feedback"
        val body = "\n\nUsername: $userName\nAndroid: $osVersion; Device model: ${Build.MODEL}\nApp version: $appVersion\n"
        sendEmailUseCase(
            email = SUPPORT_EMAIL,
            subject = subject,
            body = body
        )
    }
}