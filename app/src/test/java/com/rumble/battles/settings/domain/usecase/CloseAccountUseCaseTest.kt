package com.rumble.battles.settings.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.settings.model.repository.SettingsRepository
import com.rumble.domain.settings.domain.usecase.CloseAccountUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test


internal class CloseAccountUseCaseTest {
    private val repository = mockk<SettingsRepository>(relaxed = true)
    private val rumbleErrorUseCase = mockk<RumbleErrorUseCase>(relaxed = true)
    private val useCase = CloseAccountUseCase(repository, rumbleErrorUseCase)

    @Test
    operator fun invoke() = runBlocking {
        useCase.invoke()
        coVerify { repository.closeAccount() }
    }
}