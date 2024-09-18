package com.rumble.battles.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.SettingsEmailTag
import com.rumble.battles.settings.presentation.ChangeEmailHandler
import com.rumble.battles.settings.presentation.ChangeEmailScreen
import com.rumble.battles.settings.presentation.EmailUIState
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChangeEmailScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<ChangeEmailHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.uiState } returns MutableStateFlow(EmailUIState("email", "password"))
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                ChangeEmailScreen(
                    changeEmailHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(SettingsEmailTag).assertIsDisplayed()
    }
}