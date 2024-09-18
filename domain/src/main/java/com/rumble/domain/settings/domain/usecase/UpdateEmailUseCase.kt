package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.settings.domain.domainmodel.UpdateUserDetailsResult
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class UpdateEmailUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(email: String, password: String): UpdateUserDetailsResult {
        val result = settingsRepository.updateEmail(email, password)
        if (result.success.not()) rumbleErrorUseCase(result.rumbleError)
        return result
    }
}