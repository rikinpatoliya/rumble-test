package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.settings.domain.domainmodel.AuthProvidersResult
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class GetAuthProvidersUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): AuthProvidersResult =
        settingsRepository.fetchAuthProviders()
}