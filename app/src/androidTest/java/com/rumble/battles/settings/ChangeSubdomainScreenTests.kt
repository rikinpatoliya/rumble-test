package com.rumble.battles.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.SettingsSubdomainTag
import com.rumble.battles.settings.presentation.ChangeSubdomainHandler
import com.rumble.battles.settings.presentation.ChangeSubdomainScreen
import com.rumble.battles.settings.presentation.ChangeSubdomainUIState
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChangeSubdomainScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<ChangeSubdomainHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.uiState } returns MutableStateFlow(ChangeSubdomainUIState("mockdomain"))
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                ChangeSubdomainScreen(
                    changeSubdomainHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(SettingsSubdomainTag).assertIsDisplayed()
    }
}