package com.rumble.battles.discover

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.rumble.battles.CategoriesBrowse
import com.rumble.battles.commonViews.AlertDialogState
import com.rumble.battles.content.presentation.BottomSheetUIState
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.discover.presentation.categories.BrowseCategoriesScreen
import com.rumble.battles.discover.presentation.categories.CategoryHandler
import com.rumble.battles.discover.presentation.categories.CategoryState
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.theme.RumbleTheme
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class BrowseCategoriesScreenTests {

    @get:Rule
    val composeRule = createComposeRule()

    private val mockHandler = mockk<CategoryHandler>(relaxed = true)
    private val mockContentHandler = mockk<ContentHandler>(relaxed = true)
    private val mockAlertDialogState = mockk<AlertDialogState>(relaxed = true)
    private val mockBottomSheetUIState: BottomSheetUIState = mockk(relaxed = true)

    @Before
    fun setup() {
        every { mockHandler.state } returns MutableStateFlow(CategoryState(displayType = CategoryDisplayType.CATEGORIES))
        every { mockHandler.alertDialogState } returns mutableStateOf(mockAlertDialogState)
        every { mockContentHandler.bottomSheetUiState } returns MutableStateFlow(
            mockBottomSheetUIState
        )
    }

    @Test
    fun testScreenId() {
        composeRule.setContent {
            RumbleTheme {
                BrowseCategoriesScreen(
                    categoryHandler = mockHandler,
                    contentHandler = mockContentHandler,
                    onBackClick = {},
                    onSearch = {},
                    onChannelClick = {},
                    onVideoClick = {},
                    onViewCategory = {},
                )
            }
        }
        composeRule.onNodeWithTag(CategoriesBrowse).assertIsDisplayed()
    }
}