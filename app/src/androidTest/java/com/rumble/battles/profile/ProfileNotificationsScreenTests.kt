package com.rumble.battles.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.NotificationsTag
import com.rumble.battles.profile.presentation.ProfileNotificationsHandler
import com.rumble.battles.profile.presentation.ProfileNotificationsScreen
import com.rumble.battles.profile.presentation.ProfileNotificationsUIState
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProfileNotificationsScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<ProfileNotificationsHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.uiState } returns MutableStateFlow(ProfileNotificationsUIState())
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                ProfileNotificationsScreen(
                    profileNotificationsHandler = mockHandler,
                    onBackClick = {},
                    onChannelClick = {},
                    onVideoClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(NotificationsTag).assertIsDisplayed()
    }
}