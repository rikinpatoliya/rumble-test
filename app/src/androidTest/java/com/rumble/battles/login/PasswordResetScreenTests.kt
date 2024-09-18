package com.rumble.battles.login

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.AuthResetPass
import com.rumble.battles.login.presentation.PasswordResetHandler
import com.rumble.battles.login.presentation.PasswordResetScreen
import com.rumble.battles.login.presentation.PasswordResetState
import com.rumble.battles.login.presentation.UserOrEmailError
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PasswordResetScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<PasswordResetHandler>(relaxed = true)
    private val mockState = mockk<PasswordResetState>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.state } returns mutableStateOf(mockState)
        every { mockHandler.userNameEmailError } returns mutableStateOf(UserOrEmailError.None)
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                PasswordResetScreen(
                    passwordResetHandler = mockHandler,
                    onBack = {},
                )
            }
        }
        composeRule.onNodeWithTag(AuthResetPass).assertIsDisplayed()
    }
}