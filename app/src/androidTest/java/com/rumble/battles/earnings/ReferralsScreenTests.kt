package com.rumble.battles.earnings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.ReferralsTag
import com.rumble.battles.referrals.presentation.ReferralsHandler
import com.rumble.battles.referrals.presentation.ReferralsScreen
import com.rumble.battles.referrals.presentation.ReferralsState
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReferralsScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<ReferralsHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.state } returns MutableStateFlow(ReferralsState())
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                ReferralsScreen(
                    handler = mockHandler,
                    onBackClick = {},
                    onChannelClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(ReferralsTag).assertIsDisplayed()
    }
}