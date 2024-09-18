package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class UnlinkAuthProviderUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(loginType: LoginType): Boolean =
        settingsRepository.unlinkAuthProvider(loginType)
}