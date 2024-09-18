package com.rumble.battles.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.NavController
import com.rumble.battles.AuthSignUp
import com.rumble.battles.AuthUsername
import com.rumble.battles.commonViews.AlertDialogState
import com.rumble.battles.login.presentation.RegisterHandler
import com.rumble.battles.login.presentation.RegisterScreen
import com.rumble.battles.login.presentation.RegistrationScreenUIState
import com.rumble.battles.login.presentation.UserRegistrationEntity
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegisterScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val registerHandler = mockk<RegisterHandler>(relaxed = true)
    private val mockNavController = mockk<NavController>(relaxed = true)

    @Before
    fun setup() {
        every { registerHandler.alertDialogState } returns MutableStateFlow(
            AlertDialogState()
        )
    }

    @Test
    fun testScreenIdSsoRegistration() {
        every { registerHandler.uiState } returns MutableStateFlow(
            RegistrationScreenUIState(
                UserRegistrationEntity(),
                ssoRegistration = true,
            )
        )
        composeRule.setContent {
            RumbleTheme {
                RegisterScreen(
                    registerHandler = registerHandler,
                    navController = mockNavController,
                )
            }
        }
        composeRule.onNodeWithTag(AuthUsername).assertIsDisplayed()
    }

    @Test
    fun testScreenIdRumbleRegistration() {
        every { registerHandler.uiState } returns MutableStateFlow(
            RegistrationScreenUIState(
                UserRegistrationEntity(),
                ssoRegistration = false,
            )
        )
        composeRule.setContent {
            RumbleTheme {
                RegisterScreen(
                    registerHandler = registerHandler,
                    navController = mockNavController,
                )
            }
        }
        composeRule.onNodeWithTag(AuthSignUp).assertIsDisplayed()
    }
}