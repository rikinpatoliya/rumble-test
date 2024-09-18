//package com.rumble.battles.channels.channeldetails.presentation
//
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithTag
//import com.rumble.battles.*
//import com.rumble.battles.commonViews.AlertDialogState
//import com.rumble.theme.RumbleTheme
//import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
//import io.mockk.every
//import io.mockk.mockk
//import kotlinx.coroutines.flow.MutableStateFlow
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//internal class ChannelDetailsScreenKtTest {
//    @get:Rule
//    val composeRule = createComposeRule()
//
//    private val mockHandler = mockk<ChannelDetailsHandler>(relaxed = true)
//    private val mockChannelDetailsEntity = mockk<ChannelDetailsEntity>(relaxed = true)
//
//    @Before
//    fun setup() {
//        every { mockHandler.uiState } returns MutableStateFlow(
//            ChannelDetailsUIState(
//                "test",
//                mockChannelDetailsEntity,
//                loading = false
//            )
//        )
//        every { mockHandler.updatedEntity } returns MutableStateFlow(null)
//        every { mockHandler.alertDialogState } returns MutableStateFlow(AlertDialogState())
//        every { mockHandler.popupState } returns MutableStateFlow<ChannelDetailsDialog>(
//            ChannelDetailsDialog.ActionMenuDialog
//        )
//    }
//
//    @Test
//    fun testLoadingState() {
//        every { mockHandler.uiState } returns MutableStateFlow(
//            ChannelDetailsUIState(
//                "test",
//                loading = true
//            )
//        )
//        composeRule.setContent {
//            RumbleTheme {
//                ChannelDetailsScreen(
//                    currentDestinationRoute = "",
//                    channelDetailsHandler = mockHandler,
//                    onBackClick = {},
//                    onChannelNotification = {},
//                    onVideoClick = {}
//                )
//            }
//        }
//        composeRule.onNodeWithTag(LoadingTag).assertIsDisplayed()
//    }
//
//    @Test
//    fun testHeader() {
//        composeRule.setContent {
//            RumbleTheme {
//                ChannelDetailsScreen(
//                    currentDestinationRoute = "",
//                    channelDetailsHandler = mockHandler,
//                    onBackClick = {},
//                    onChannelNotification = {},
//                    onVideoClick = {}
//                )
//            }
//        }
//        composeRule.onNodeWithTag(ChannelBackSplashTag).assertIsDisplayed()
//    }
//
//    @Test
//    fun testTopBar() {
//        composeRule.setContent {
//            RumbleTheme {
//                ChannelDetailsScreen(
//                    currentDestinationRoute = "",
//                    channelDetailsHandler = mockHandler,
//                    onBackClick = {},
//                    onChannelNotification = {},
//                    onVideoClick = {}
//                )
//            }
//        }
//        composeRule.onNodeWithTag(ChannelTopBarTag).assertIsDisplayed()
//        composeRule.onNodeWithTag(ChannelTopBarActionMenuTag).assertIsDisplayed()
//    }
//
//    @Test
//    fun testProfileImage() {
//        composeRule.setContent {
//            RumbleTheme {
//                ChannelDetailsScreen(
//                    currentDestinationRoute = "",
//                    channelDetailsHandler = mockHandler,
//                    onBackClick = {},
//                    onChannelNotification = {},
//                    onVideoClick = {}
//                )
//            }
//        }
//        composeRule.onNodeWithTag(CollapsingChannelImageTag).assertIsDisplayed()
//    }
//}