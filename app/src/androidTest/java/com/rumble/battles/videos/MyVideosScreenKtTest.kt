//package com.rumble.battles.videos
//
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithTag
//import com.rumble.battles.ChannelBackSplashTag
//import com.rumble.battles.CollapsingChannelImageTag
//import com.rumble.battles.LoadingTag
//import com.rumble.battles.MyVideosTopBarTag
//import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsUIState
//import com.rumble.theme.RumbleTheme
//import com.rumble.battles.videos.presentation.MyVideosHandler
//import com.rumble.battles.videos.presentation.MyVideosScreen
//import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
//import io.mockk.every
//import io.mockk.mockk
//import kotlinx.coroutines.flow.MutableStateFlow
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//internal class MyVideosScreenKtTest {
//    @get:Rule
//    val composeRule = createComposeRule()
//
//    private val mockHandler = mockk<MyVideosHandler>(relaxed = true)
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
//                MyVideosScreen(
//                    currentDestinationRoute = "",
//                    myVideosHandler = mockHandler,
//                    onEditProfileClick = {},
//                    onVideoClick = {},
//                    newImageUri = null
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
//                MyVideosScreen(
//                    currentDestinationRoute = "",
//                    myVideosHandler = mockHandler,
//                    onEditProfileClick = {},
//                    onVideoClick = {},
//                    newImageUri = null
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
//                MyVideosScreen(
//                    currentDestinationRoute = "",
//                    myVideosHandler = mockHandler,
//                    onEditProfileClick = {},
//                    onVideoClick = {},
//                    newImageUri = null
//                )
//            }
//        }
//        composeRule.onNodeWithTag(MyVideosTopBarTag).assertIsDisplayed()
//    }
//
//    @Test
//    fun testProfileImage() {
//        composeRule.setContent {
//            RumbleTheme {
//                MyVideosScreen(
//                    currentDestinationRoute = "",
//                    myVideosHandler = mockHandler,
//                    onEditProfileClick = {},
//                    onVideoClick = {},
//                    newImageUri = null
//                )
//            }
//        }
//        composeRule.onNodeWithTag(CollapsingChannelImageTag).assertIsDisplayed()
//    }
//}