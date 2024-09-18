package com.rumble.battles.profile.domain

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.profile.model.repository.ProfileRepository
import com.rumble.network.session.SessionManager
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class GetUserProfileUseCaseTest {
    private val repository = mockk<ProfileRepository>(relaxed = true)
    private val rumbleErrorUseCase = mockk<RumbleErrorUseCase>(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val useCase = GetUserProfileUseCase(repository, sessionManager, rumbleErrorUseCase)

    @Test
    operator fun invoke() = runBlocking {
        useCase.invoke()
        coVerify { repository.getUserProfile() }
    }
}