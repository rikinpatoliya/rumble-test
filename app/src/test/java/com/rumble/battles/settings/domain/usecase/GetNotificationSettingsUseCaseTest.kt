package com.rumble.battles.settings.domain.usecase

import com.rumble.domain.settings.model.repository.SettingsRepository
import com.rumble.domain.settings.domain.usecase.GetNotificationSettingsUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test


internal class GetNotificationSettingsUseCaseTest {
    private val repository = mockk<SettingsRepository>(relaxed = true)
    private val useCase = GetNotificationSettingsUseCase(repository)

    @Test
    operator fun invoke() = runBlocking {
        useCase.invoke()
        coVerify { repository.fetchNotificationSettings() }
    }
}