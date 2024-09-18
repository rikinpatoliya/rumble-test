package com.rumble.battles.profile.domain

import com.rumble.domain.profile.domain.GetCountriesUseCase
import com.rumble.domain.profile.model.repository.ProfileRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class GetCountriesUseCaseTest {
    private val repository = mockk<ProfileRepository>(relaxed = true)
    private val useCase = GetCountriesUseCase(repository)

    @Test
    operator fun invoke() = runBlocking {
        useCase.invoke()
        coVerify { repository.getCountries() }
    }

}