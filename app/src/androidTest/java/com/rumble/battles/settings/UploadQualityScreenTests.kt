package com.rumble.battles.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.SettingsUploadingQualityTag
import com.rumble.battles.settings.presentation.SettingsHandler
import com.rumble.battles.settings.presentation.SettingsScreenUIState
import com.rumble.battles.settings.presentation.UploadQualityScreen
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UploadQualityScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<SettingsHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.uiState } returns MutableStateFlow(
            SettingsScreenUIState(
                notificationSettingsEntity = null,
                allNotificationsEnabled = false
            )
        )
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                UploadQualityScreen(
                    settingsHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(SettingsUploadingQualityTag).assertIsDisplayed()
    }
}