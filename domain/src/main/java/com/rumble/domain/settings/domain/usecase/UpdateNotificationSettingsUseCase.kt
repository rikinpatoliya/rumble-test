package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsEntity
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsResult
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class UpdateNotificationSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(notificationSettingsEntity: NotificationSettingsEntity): NotificationSettingsResult =
        settingsRepository.updateNotificationSettings(notificationSettingsEntity)
}