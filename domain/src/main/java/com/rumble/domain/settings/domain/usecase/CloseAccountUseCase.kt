package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.settings.domain.domainmodel.CloseAccountResult
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class CloseAccountUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke(): CloseAccountResult {
        val result = settingsRepository.closeAccount()
        if (result.success.not()) rumbleErrorUseCase(result.rumbleError)
        return result
    }
}