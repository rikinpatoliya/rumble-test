package com.rumble.battles.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.rumble.battles.EmptyViewTag
import com.rumble.battles.LoadingTag
import com.rumble.battles.SearchCombinedTag
import com.rumble.battles.commonViews.AlertDialogState
import com.rumble.battles.content.presentation.BottomSheetUIState
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.search.presentation.combinedSearch.CombineSearchResultHandler
import com.rumble.battles.search.presentation.combinedSearch.CombineSearchResultScreen
import com.rumble.battles.search.presentation.combinedSearch.CombineSearchResultState
import com.rumble.battles.search.presentation.combinedSearch.SearchState
import com.rumble.battles.sort.SortFilterSelection
import com.rumble.domain.sort.DurationType
import com.rumble.domain.sort.FilterType
import com.rumble.domain.sort.SortType
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CombineSearchResultScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockQuery = "Mock query"
    private val mockHandler = mockk<CombineSearchResultHandler>(relaxed = true)
    private val mockContentHandler = mockk<ContentHandler>(relaxed = true)
    private val mockState = mockk<CombineSearchResultState>(relaxed = true)
    private val mockAlertDialogState = mockk<AlertDialogState>(relaxed = true)
    private val mockBottomSheetUIState: BottomSheetUIState = mockk(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.state } returns mutableStateOf(mockState)
        every { mockHandler.query } returns mockQuery
        every { mockHandler.selection } returns SortFilterSelection(
            SortType.LIKES,
            FilterType.THIS_WEEK,
            DurationType.ANY
        )
        every { mockHandler.alertDialogState } returns mutableStateOf(mockAlertDialogState)
        every { mockContentHandler.bottomSheetUiState } returns MutableStateFlow(
            mockBottomSheetUIState
        )
    }

    @Test
    fun testScreenId() {
        setContentView()
        composeRule.onNodeWithTag(SearchCombinedTag).assertIsDisplayed()
    }


    @Test
    fun testHeader() {
        every { mockHandler.state } returns mutableStateOf(CombineSearchResultState(searchState = SearchState.LOADING))
        setContentView()
        composeRule.onNodeWithText(mockQuery).assertIsDisplayed()
    }

    @Test
    fun testLoadingState() {
        every { mockHandler.state } returns mutableStateOf(CombineSearchResultState(searchState = SearchState.LOADING))
        setContentView()
        composeRule.onNodeWithTag(LoadingTag).assertIsDisplayed()
    }

    @Test
    fun testEmptyResultState() {
        every { mockHandler.state } returns mutableStateOf(CombineSearchResultState(searchState = SearchState.EMPTY))
        setContentView()
        composeRule.onNodeWithTag(EmptyViewTag).assertIsDisplayed()
    }

    private fun setContentView() {
        composeRule.setContent {
            RumbleTheme {
                CombineSearchResultScreen(
                    handler = mockHandler,
                    contentHandler = mockContentHandler,
                    onSearch = {},
                    onViewChannels = {},
                    onViewVideos = { _, _: SortFilterSelection -> },
                    onViewChannel = {},
                    onBack = {},
                )
            }
        }
    }
}