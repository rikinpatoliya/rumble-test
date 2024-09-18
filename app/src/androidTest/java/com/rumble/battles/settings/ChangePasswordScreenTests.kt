package com.rumble.battles.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.SettingsPasswordTag
import com.rumble.battles.settings.presentation.ChangePasswordHandler
import com.rumble.battles.settings.presentation.ChangePasswordScreen
import com.rumble.battles.settings.presentation.ChangePasswordUIState
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChangePasswordScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<ChangePasswordHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.uiState } returns MutableStateFlow(
            ChangePasswordUIState(
                "newPassword",
                "currentPassword"
            )
        )
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                ChangePasswordScreen(
                    changePasswordHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(SettingsPasswordTag).assertIsDisplayed()
    }
}