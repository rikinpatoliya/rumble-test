package com.rumble.battles.settings.domain.usecase

import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsEntity
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsLegacyEntity
import com.rumble.domain.settings.domain.usecase.UpdateNotificationSettingsUseCase
import com.rumble.domain.settings.model.repository.SettingsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class UpdateNotificationSettingsUseCaseTest {
    private val repository = mockk<SettingsRepository>(relaxed = true)
    private val useCase = UpdateNotificationSettingsUseCase(repository)

    @Test
    operator fun invoke() = runBlocking {
        val entity = NotificationSettingsEntity(
            NotificationSettingsLegacyEntity(
                true,
                true,
                true,
                true,
                true,
                true,
                true
            ), true, true, true, true, true, true, true,
        )
        useCase.invoke(entity)
        coVerify { repository.updateNotificationSettings(entity) }
    }
}