package com.rumble.battles.search

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.SearchVideosTag
import com.rumble.battles.commonViews.AlertDialogState
import com.rumble.battles.content.presentation.BottomSheetUIState
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.search.presentation.videosSearch.VideosSearchHandler
import com.rumble.battles.search.presentation.videosSearch.VideosSearchScreen
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

class VideoSearchScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<VideosSearchHandler>(relaxed = true)
    private val mockContentHandler = mockk<ContentHandler>(relaxed = true)
    private val mockAlertDialogState = mockk<AlertDialogState>(relaxed = true)
    private val mockBottomSheetUIState: BottomSheetUIState = mockk(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.query } returns "some video query"
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
        composeRule.setContent {
            RumbleTheme {
                VideosSearchScreen(
                    handler = mockHandler,
                    contentHandler = mockContentHandler,
                    onSearch = {},
                    onViewVideo = {},
                    onBack = {},
                    onImpression = {},
                )
            }
        }
        composeRule.onNodeWithTag(SearchVideosTag).assertIsDisplayed()
    }
}