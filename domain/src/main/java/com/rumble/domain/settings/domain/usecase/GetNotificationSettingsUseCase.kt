package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsResult
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class GetNotificationSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): NotificationSettingsResult =
        settingsRepository.fetchNotificationSettings()
}