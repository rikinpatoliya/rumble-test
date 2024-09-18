package com.rumble.battles.login

import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.rumble.battles.login.presentation.LoginScreenError
import com.rumble.battles.login.presentation.LoginViewModel
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.login.domain.usecases.RumbleLoginUseCase
import com.rumble.domain.login.domain.usecases.SSOLoginUseCase
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class LoginViewModelTests {

    private val mockGoogleSignInClient = mockk<GoogleSignInClient>(relaxed = true)
    private val mockSSOLoginUseCase = mockk<SSOLoginUseCase>(relaxed = true)
    private val mockRumbleLoginUseCase = mockk<RumbleLoginUseCase>(relaxed = true)
    private val unhandledErrorUseCase: UnhandledErrorUseCase = mockk(relaxed = true)

    private lateinit var viewModel: LoginViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = LoginViewModel(
            googleSignInClient = mockGoogleSignInClient,
            ssoLoginUseCase = mockSSOLoginUseCase,
            rumbleLoginUseCase = mockRumbleLoginUseCase,
            unhandledErrorUseCase = unhandledErrorUseCase,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testOnUserNameChanged() {
        viewModel.onUserNameChanged("test")
        assert(viewModel.userNameEmailError.value == LoginScreenError.None)
    }

    @Test
    fun testOnPasswordChanged() {
        viewModel.onPasswordChanged("test")
        assert(viewModel.passwordError.value == LoginScreenError.None)
    }

    @Test
    fun testOnSignInNoInputErrorsSuccess() {
        viewModel.onUserNameChanged("test")
        viewModel.onPasswordChanged("test")
        viewModel.onSignIn()
        assert(viewModel.userNameEmailError.value == LoginScreenError.None)
        assert(viewModel.passwordError.value == LoginScreenError.None)
    }

    @Test
    fun testOnSignInNoInputErrorsFailure() {
        viewModel.onUserNameChanged("test")
        viewModel.onPasswordChanged("test")
        viewModel.onSignIn()
        assert(viewModel.userNameEmailError.value == LoginScreenError.None)
        assert(viewModel.passwordError.value == LoginScreenError.None)
    }

    @Test
    fun testOnSignInWithInputErrors() {
        viewModel.onUserNameChanged("")
        viewModel.onPasswordChanged("")
        viewModel.onSignIn()
        assert(viewModel.userNameEmailError.value == LoginScreenError.InputError)
        assert(viewModel.passwordError.value == LoginScreenError.InputError)
    }
}