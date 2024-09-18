package com.rumble.battles.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.rumble.battles.SearchQueryTag
import com.rumble.battles.search.presentation.searchScreen.SearchHandler
import com.rumble.battles.search.presentation.searchScreen.SearchQueryUIState
import com.rumble.battles.search.presentation.searchScreen.SearchScreen
import com.rumble.domain.search.domain.domainModel.RecentQuery
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockQuery = "TEST QUERY"
    private val testRecentQuery = RecentQuery(query = mockQuery)
    private val mockHandler = mockk<SearchHandler>(relaxed = true)
    private val mockState = mockk<SearchQueryUIState>(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.state } returns MutableStateFlow(mockState)
        every { mockHandler.navDest } returns ""
        every { mockHandler.parentScreen } returns ""
        every { mockHandler.initialQuery } returns ""
    }

    @Test
    fun testScreenId() {
        setContentView()
        composeRule.onNodeWithTag(SearchQueryTag).assertIsDisplayed()
    }

    @Test
    fun testListOfRecentSearches() {
        every { mockHandler.state } returns MutableStateFlow(
            SearchQueryUIState(
                recentQueryList = listOf(
                    testRecentQuery
                )
            )
        )
        setContentView()
        composeRule.onNodeWithText(mockQuery).assertIsDisplayed()
    }

    private fun setContentView() {
        composeRule.setContent {
            RumbleTheme {
                SearchScreen(
                    searchHandler = mockHandler,
                    onSearch = { _, _, _ -> },
                    onViewChannel = {},
                    onBrowseCategory = {},
                    onCancel = {},
                )
            }
        }
    }
}