package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.settings.domain.domainmodel.License
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class CreditsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {

    suspend operator fun invoke(): List<License> =
        settingsRepository.fetchLicenseList().filterNot {
            (it.componentName.isEmpty() && it.licenseName.isEmpty()) ||
                    (it.licenseName.isEmpty() && it.licenseUrl.isEmpty())
        }
}