package com.rumble.battles.earnings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.EarningsTag
import com.rumble.battles.earnings.presentation.EarningsHandler
import com.rumble.battles.earnings.presentation.EarningsScreen
import com.rumble.battles.earnings.presentation.EarningsState
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EarningsScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<EarningsHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.state } returns MutableStateFlow(EarningsState())
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                EarningsScreen(
                    earningsHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(EarningsTag).assertIsDisplayed()
    }
}