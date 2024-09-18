package com.rumble.battles.settings.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.settings.model.repository.SettingsRepository
import com.rumble.domain.settings.domain.usecase.UpdateEmailUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class UpdateEmailUseCaseTest {
    private val repository = mockk<SettingsRepository>(relaxed = true)
    private val rumbleErrorUseCase = mockk<RumbleErrorUseCase>(relaxed = true)
    private val useCase = UpdateEmailUseCase(repository, rumbleErrorUseCase)

    @Test
    operator fun invoke() = runBlocking {
        val email = "newEmail"
        val password = "currentPassword"
        useCase.invoke(email, password)
        coVerify { repository.updateEmail(email, password) }
    }
}