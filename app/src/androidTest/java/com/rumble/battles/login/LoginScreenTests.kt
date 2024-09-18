package com.rumble.battles.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavController
import com.rumble.battles.AuthSignIn
import com.rumble.battles.AuthSignInLoginInput
import com.rumble.battles.AuthSignInPasswordInput
import com.rumble.battles.AuthSignInSignInButton
import com.rumble.battles.InputFieldErrorTag
import com.rumble.battles.LoadingTag
import com.rumble.battles.LoginPasswordErrorTag
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.login.presentation.LoginHandler
import com.rumble.battles.login.presentation.LoginScreen
import com.rumble.battles.login.presentation.LoginScreenError
import com.rumble.battles.login.presentation.LoginScreenState
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<LoginHandler>(relaxed = true)
    private val activityHandler = mockk<RumbleActivityHandler>(relaxed = true)
    private val mockState = mockk<LoginScreenState>(relaxed = true)
    private val mockNavController = mockk<NavController>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.state } returns mutableStateOf(mockState)
    }

    @Test
    fun testInitialState() {
        every { mockHandler.passwordError } returns mutableStateOf(LoginScreenError.None)
        every { mockHandler.userNameEmailError } returns mutableStateOf(LoginScreenError.None)
        composeRule.setContent {
            RumbleTheme {
                LoginScreen(
                    loginHandler = mockHandler,
                    activityHandler = activityHandler,
                    navController = mockNavController,
                    onForgotPassword = {},
                )
            }
        }
        composeRule.onNodeWithTag(AuthSignIn).assertIsDisplayed()
        composeRule.onNodeWithTag(AuthSignInLoginInput).assertIsDisplayed()
        composeRule.onNodeWithTag(AuthSignInPasswordInput).assertIsDisplayed()
        composeRule.onNodeWithTag(AuthSignInSignInButton).assertIsDisplayed()

        composeRule.onNodeWithTag(LoadingTag).assertDoesNotExist()
        composeRule.onNodeWithTag(LoginPasswordErrorTag).assertDoesNotExist()
        composeRule.onNodeWithTag(InputFieldErrorTag).assertDoesNotExist()
    }

    @Test
    fun testInputErrors() {
        every { mockHandler.passwordError } returns mutableStateOf(LoginScreenError.InputError)
        every { mockHandler.userNameEmailError } returns mutableStateOf(LoginScreenError.InputError)
        composeRule.setContent {
            RumbleTheme {
                LoginScreen(
                    loginHandler = mockHandler,
                    activityHandler = activityHandler,
                    navController = mockNavController,
                    onForgotPassword = {},
                )
            }
        }
        composeRule.onNodeWithTag(LoadingTag).assertDoesNotExist()
        composeRule.onNodeWithTag(LoginPasswordErrorTag).assertIsDisplayed()
        composeRule.onNodeWithTag(InputFieldErrorTag).assertIsDisplayed()
    }

    @Test
    fun testLoadingState() {
        every { mockState.loading } returns true
        every { mockHandler.passwordError } returns mutableStateOf(LoginScreenError.None)
        every { mockHandler.userNameEmailError } returns mutableStateOf(LoginScreenError.None)
        composeRule.setContent {
            RumbleTheme {
                LoginScreen(
                    loginHandler = mockHandler,
                    activityHandler = activityHandler,
                    navController = mockNavController,
                    onForgotPassword = {},
                )
            }
        }
        composeRule.onNodeWithTag(LoadingTag).assertIsDisplayed()
    }
}