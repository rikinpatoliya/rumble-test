package com.rumble.battles.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.SearchChannelsTag
import com.rumble.battles.search.presentation.channelsSearch.ChannelSearchHandler
import com.rumble.battles.search.presentation.channelsSearch.ChannelSearchScreen
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChannelSearchScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<ChannelSearchHandler>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.query } returns "some query"
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                ChannelSearchScreen(
                    handler = mockHandler,
                    onSearch = {},
                    onViewChannel = {},
                    onBack = {},
                )
            }
        }
        composeRule.onNodeWithTag(SearchChannelsTag).assertIsDisplayed()
    }
}