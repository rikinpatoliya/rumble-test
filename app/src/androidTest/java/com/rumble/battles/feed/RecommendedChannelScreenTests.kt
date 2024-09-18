package com.rumble.battles.discover

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.RecommendedChannelsTag
import com.rumble.battles.content.presentation.BottomSheetUIState
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.feed.presentation.recommended_channels.RecommendedChannelScreen
import com.rumble.battles.feed.presentation.recommended_channels.RecommendedChannelsHandler
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RecommendedChannelScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<RecommendedChannelsHandler>(relaxed = true)
    private val mockContentHandler = mockk<ContentHandler>(relaxed = true)
    private val mockBottomSheetUIState: BottomSheetUIState = mockk(relaxed = true)

    @Before
    fun setup() {
        every { mockContentHandler.bottomSheetUiState } returns MutableStateFlow(
            mockBottomSheetUIState
        )
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                RecommendedChannelScreen(
                    contentHandler = mockContentHandler,
                    recommendedChannelsHandler = mockHandler,
                    title = "Recommended Channels",
                    onChannelClick = {},
                    onBackClick = {},
                )
            }
        }
        composeRule.onNodeWithTag(RecommendedChannelsTag).assertIsDisplayed()
    }
}