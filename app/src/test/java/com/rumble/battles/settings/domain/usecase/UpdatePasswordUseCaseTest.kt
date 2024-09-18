package com.rumble.battles.settings.domain.usecase

import com.rumble.domain.settings.model.repository.SettingsRepository
import com.rumble.domain.settings.domain.usecase.UpdatePasswordUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class UpdatePasswordUseCaseTest {
    private val repository = mockk<SettingsRepository>(relaxed = true)
    private val useCase = UpdatePasswordUseCase(repository)

    @Test
    operator fun invoke() = runBlocking {
        val newPassword = "new"
        val currentPassword = "current"
        useCase.invoke(newPassword, currentPassword)
        coVerify { repository.updatePassword(newPassword, currentPassword) }
    }
}