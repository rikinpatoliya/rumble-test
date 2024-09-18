package com.rumble.battles.settings.presentation

import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.usecase.UpdatePasswordUseCase
import com.rumble.domain.validation.usecases.PasswordValidationUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test


internal class ChangePasswordViewModelTest {

    private val updatePasswordUseCase =
        mockk<UpdatePasswordUseCase>(relaxed = true)
    private val passwordValidationUseCase = mockk<PasswordValidationUseCase>(relaxed = true)
    private val unhandledErrorUseCase: UnhandledErrorUseCase = mockk(relaxed = true)

    private val validNewPassword = "123456789"
    private val inValidNewPassword = "1234567"
    private val validCurrentPassword = "12345678"
    private val inValidCurrentPassword = ""

    private lateinit var viewModel: ChangePasswordViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = ChangePasswordViewModel(updatePasswordUseCase, passwordValidationUseCase, unhandledErrorUseCase)
        every { passwordValidationUseCase.invoke(validNewPassword) } returns true
        every { passwordValidationUseCase.invoke(validCurrentPassword) } returns true
        every { passwordValidationUseCase.invoke(inValidNewPassword) } returns false
        every { passwordValidationUseCase.invoke(inValidCurrentPassword) } returns false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onNewPasswordChangedWithError() {
        viewModel.onNewPasswordChanged(inValidNewPassword)
        viewModel.onUpdate()
        assert(viewModel.uiState.value.newPasswordError)
    }

    @Test
    fun onNewPasswordChangedValid() {
        viewModel.onNewPasswordChanged(validNewPassword)
        viewModel.onUpdate()
        assert(!viewModel.uiState.value.newPasswordError)
    }

    @Test
    fun onCurrentPasswordChangedWithError() {
        viewModel.onCurrentPasswordChanged(inValidCurrentPassword)
        viewModel.onUpdate()
        assert(viewModel.uiState.value.currentPasswordError)
    }

    @Test
    fun onCurrentPasswordChangedValid() {
        viewModel.onCurrentPasswordChanged(validCurrentPassword)
        viewModel.onUpdate()
        assert(!viewModel.uiState.value.currentPasswordError)
    }

    @Test
    fun onUpdate() {
        viewModel.onNewPasswordChanged(validNewPassword)
        viewModel.onCurrentPasswordChanged(validCurrentPassword)
        viewModel.onUpdate()
        coVerify { updatePasswordUseCase.invoke(validNewPassword, validCurrentPassword) }
    }

    @Test
    fun onDismissDialog() {
        viewModel.onDismissDialog()
        assert(viewModel.uiState.value.alertDialogResponseData == null)
    }
}