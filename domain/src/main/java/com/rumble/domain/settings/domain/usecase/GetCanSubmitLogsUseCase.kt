package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.settings.domain.domainmodel.CanSubmitLogsResult
import com.rumble.domain.settings.model.repository.SettingsRepository
import javax.inject.Inject

class GetCanSubmitLogsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val isDevelopModeUseCase: IsDevelopModeUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(): CanSubmitLogsResult {
        val result = if (isDevelopModeUseCase()) {
            CanSubmitLogsResult.Success(canSubmitLogs = true)
        } else {
            settingsRepository.fetchCanSubmitLogs()
        }

        if (result is CanSubmitLogsResult.Failure)
            rumbleErrorUseCase(result.rumbleError)

        return result
    }
}