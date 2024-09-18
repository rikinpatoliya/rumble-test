package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.settings.domain.domainmodel.UpdateUserDetailsResult
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class UpdatePasswordUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(newPassword: String, currentPassword: String): UpdateUserDetailsResult =
        settingsRepository.updatePassword(newPassword, currentPassword)
}