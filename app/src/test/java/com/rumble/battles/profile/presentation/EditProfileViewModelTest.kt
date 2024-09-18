package com.rumble.battles.profile.presentation

import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.profile.domain.GetCountriesUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.profile.domain.UpdateUserImageUseCase
import com.rumble.domain.profile.domain.UpdateUserProfileUseCase
import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.domain.validation.usecases.EmailValidationUseCase
import com.rumble.network.session.SessionManager
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class EditProfileViewModelTest {

    private val sessionManager = mockk<SessionManager>(relaxed = true)
    private val getUserProfileUseCase = mockk<GetUserProfileUseCase>(relaxed = true)
    private val updateUserProfileUseCase = mockk<UpdateUserProfileUseCase>(relaxed = true)
    private val updateUserImageUseCase = mockk<UpdateUserImageUseCase>(relaxed = true)
    private val getCountriesUseCase = mockk<GetCountriesUseCase>(relaxed = true)
    private val emailValidationUseCase = mockk<EmailValidationUseCase>(relaxed = true)
    private val unhandledErrorUseCase: UnhandledErrorUseCase = mockk(relaxed = true)

    private lateinit var viewModel: EditProfileViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = EditProfileViewModel(
            sessionManager,
            getUserProfileUseCase,
            updateUserProfileUseCase,
            updateUserImageUseCase,
            getCountriesUseCase,
            emailValidationUseCase,
            unhandledErrorUseCase,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onCountryChanged() {
        val country = CountryEntity(2, "Canada")
        viewModel.onCountryChanged(country)
        assert(viewModel.uiState.value.userProfileEntity.country == country)
    }

    @Test
    fun onSelectCountry() {
        runBlocking {
            viewModel.onSelectCountry()
            assert(viewModel.vmEvents.first() == EditProfileVmEvent.ShowCountrySelection)
        }
    }
}