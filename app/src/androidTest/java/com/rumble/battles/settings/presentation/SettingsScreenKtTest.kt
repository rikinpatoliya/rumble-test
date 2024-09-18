package com.rumble.battles.settings.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.AndroidIdSettingsTag
import com.rumble.battles.AppInfoSectionTag
import com.rumble.battles.BackgroundPlaySectionTag
import com.rumble.battles.DebugSectionTag
import com.rumble.battles.LoadingTag
import com.rumble.battles.NotificationsSectionTag
import com.rumble.battles.RumbleBasicTopAppBarTag
import com.rumble.battles.SettingsTag
import com.rumble.battles.UserDetailsSectionTag
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsEntity
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

internal class SettingsScreenKtTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<SettingsHandler>(relaxed = true)
    private val mockNotificationSettingsEntity = mockk<NotificationSettingsEntity>(relaxed = true)

    @Test
    fun testScreenId() {
        every { mockHandler.uiState } returns MutableStateFlow(
            SettingsScreenUIState(
                mockNotificationSettingsEntity,
                allNotificationsEnabled = false,
                loading = false
            )
        )
        composeRule.setContent {
            RumbleTheme {
                SettingsScreen(
                    settingsHandler = mockHandler,
                    onBackClick = {},
                    onNavigate = {}
                )
            }
        }
        composeRule.onNodeWithTag(AndroidIdSettingsTag).assertIsDisplayed()
    }

    @Test
    fun testTopBar() {
        every { mockHandler.uiState } returns MutableStateFlow(
            SettingsScreenUIState(
                mockNotificationSettingsEntity,
                allNotificationsEnabled = false,
                loading = false
            )
        )
        composeRule.setContent {
            RumbleTheme {
                SettingsScreen(
                    settingsHandler = mockHandler,
                    onBackClick = {},
                    onNavigate = {}
                )
            }
        }
        composeRule.onNodeWithTag(RumbleBasicTopAppBarTag).assertIsDisplayed()
    }

    @Test
    fun testLoadingState() {
        every { mockHandler.uiState } returns MutableStateFlow(
            SettingsScreenUIState(
                mockNotificationSettingsEntity,
                allNotificationsEnabled = false,
                loading = true
            )
        )
        composeRule.setContent {
            RumbleTheme {
                SettingsScreen(
                    settingsHandler = mockHandler,
                    onBackClick = {},
                    onNavigate = {}
                )
            }
        }
        composeRule.onNodeWithTag(LoadingTag).assertIsDisplayed()
    }

    @Test
    fun testNotificationsSections() {
        every { mockHandler.uiState } returns MutableStateFlow(
            SettingsScreenUIState(
                mockNotificationSettingsEntity,
                allNotificationsEnabled = true,
                loading = false
            )
        )
        composeRule.setContent {
            RumbleTheme {
                SettingsScreen(
                    settingsHandler = mockHandler,
                    onBackClick = {},
                    onNavigate = {}
                )
            }
        }
        composeRule.onNodeWithTag(NotificationsSectionTag).assertIsDisplayed()
    }

    @Test
    fun testUISections() {
        every { mockHandler.uiState } returns MutableStateFlow(
            SettingsScreenUIState(
                null,
                allNotificationsEnabled = false,
                loading = false
            )
        )
        composeRule.setContent {
            RumbleTheme {
                SettingsScreen(
                    settingsHandler = mockHandler,
                    onBackClick = {},
                    onNavigate = {}
                )
            }
        }
        composeRule.onNodeWithTag(BackgroundPlaySectionTag).assertIsDisplayed()
        composeRule.onNodeWithTag(UserDetailsSectionTag).assertIsDisplayed()
        composeRule.onNodeWithTag(AppInfoSectionTag).assertIsDisplayed()
    }

    @Test
    fun testDebugSection() {
        every { mockHandler.uiState } returns MutableStateFlow(
            SettingsScreenUIState(
                null,
                allNotificationsEnabled = false,
                loading = false,
                debugState = DebugUIState(canUseSubdomain = true)
            )
        )
        composeRule.setContent {
            RumbleTheme {
                SettingsScreen(
                    settingsHandler = mockHandler,
                    onBackClick = {},
                    onNavigate = {}
                )
            }
        }
        composeRule.onNodeWithTag(DebugSectionTag).assertIsDisplayed()
    }
}