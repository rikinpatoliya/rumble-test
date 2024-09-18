package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.landing.usecases.GetUserCookiesUseCase
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class GetCanUseAdsDebugMode @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val isDevelopModeUseCase: IsDevelopModeUseCase,
    private val getUserCookiesUseCase: GetUserCookiesUseCase,
) {

    suspend operator fun invoke(): Boolean =
        when {
            isDevelopModeUseCase() -> true

            getUserCookiesUseCase().isNotEmpty() -> {
                val result = settingsRepository.fetchNotificationSettings()
                result.success && result.canUseCustomApiDomain
            }

            else -> false
        }
}