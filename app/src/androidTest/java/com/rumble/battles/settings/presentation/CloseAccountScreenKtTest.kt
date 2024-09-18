package com.rumble.battles.settings.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.CloseAccountActionButtonTag
import com.rumble.battles.CloseAccountTag
import com.rumble.battles.CloseAccountTextTag
import com.rumble.battles.LoadingTag
import com.rumble.battles.RumbleBasicTopAppBarTag
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class CloseAccountScreenKtTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<CloseAccountHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.uiState } returns MutableStateFlow(
            CloseAccountUIState()
        )
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                CloseAccountScreen(
                    closeAccountHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(CloseAccountTag).assertIsDisplayed()
    }

    @Test
    fun testTopBar() {
        composeRule.setContent {
            RumbleTheme {
                CloseAccountScreen(
                    closeAccountHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(RumbleBasicTopAppBarTag).assertIsDisplayed()
    }

    @Test
    fun testLoadingState() {
        every { mockHandler.uiState } returns MutableStateFlow(
            CloseAccountUIState(loading = true)
        )
        composeRule.setContent {
            RumbleTheme {
                CloseAccountScreen(
                    closeAccountHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(LoadingTag).assertIsDisplayed()
    }

    @Test
    fun testUIContent() {
        composeRule.setContent {
            RumbleTheme {
                CloseAccountScreen(
                    closeAccountHandler = mockHandler,
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(CloseAccountTextTag).assertIsDisplayed()
        composeRule.onNodeWithTag(CloseAccountActionButtonTag).assertIsDisplayed()
    }
}