package com.rumble.battles.profile.domain

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.profile.domain.UpdateUserProfileUseCase
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.profile.model.repository.ProfileRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDate

internal class UpdateUserProfileUseCaseTest {
    private val repository = mockk<ProfileRepository>(relaxed = true)
    private val rumbleErrorUseCase = mockk<RumbleErrorUseCase>(relaxed = true)
    private val useCase = UpdateUserProfileUseCase(repository, rumbleErrorUseCase)

    @Test
    operator fun invoke() = runBlocking {
        val entity = UserProfileEntity("", "", "", true, "", "", "", "", "", "", CountryEntity(0, ""), "", 0, false, Gender.Unspecified, LocalDate.now())
        useCase.invoke(entity)
        coVerify { repository.updateUserProfile(entity) }
    }
}