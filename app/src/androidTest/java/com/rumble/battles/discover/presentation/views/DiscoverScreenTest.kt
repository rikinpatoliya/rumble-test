//package com.rumble.battles.discover.presentation.views
//
//import androidx.compose.ui.test.junit4.createComposeRule
//import com.rumble.battles.*
//import com.rumble.battles.discover.presentation.DiscoverHandler
//import com.rumble.battles.discover.presentation.DiscoverScreen
//import com.rumble.battles.discover.presentation.DiscoverState
//import com.rumble.theme.RumbleTheme
//import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
//import io.mockk.every
//import io.mockk.mockk
//import kotlinx.coroutines.flow.MutableStateFlow
//import org.junit.Rule
//import org.junit.Test
//
//internal class DiscoverScreenTest {
//
//    @get:Rule
//    val composeRule = createComposeRule()
//
//    private val mockHandler = mockk<DiscoverHandler>(relaxed = true)
//
//    @Test
//    fun testLoadingState() {
//        every { mockHandler.state } returns MutableStateFlow(
//            DiscoverState(
//                justForYouLoading = true,
//                liveNowLoading = true,
//                editorPicksLoading = true,
//                featuredChannelsLoading = true,
//                doNotMissItLoading = true,
//                popularVideosLoading = true
//            )
//        )
//        composeRule.setContent {
//            RumbleTheme {
//                DiscoverScreen(
//                    discoverHandler = mockHandler
//                )
//            }
//        }
//
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, JustForYouLoadingTag)
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, LiveNowLoadingTag)
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, EditorPicksLoadingTag)
//        composeRule.scrollToAndAssertDisplayed(
//            DiscoverMainContentColumn,
//            FeaturedChannelsLoadingTag
//        )
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, DoNotMissItLoadingTag)
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, PopularVideosLoadingTag)
//    }
//
//    @Test
//    fun testErrorState() {
//        every { mockHandler.state } returns MutableStateFlow(
//            DiscoverState(
//                justForYouError = true,
//                liveNowError = true,
//                editorPicksError = true,
//                featuredChannelsError = true,
//                doNotMissItError = true,
//                popularVideosError = true
//            )
//        )
//        composeRule.setContent {
//            RumbleTheme {
//                DiscoverScreen(
//                    discoverHandler = mockHandler
//                )
//            }
//        }
//
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, JustForYouErrorTag)
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, LiveNowErrorTag)
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, EditorPicksErrorTag)
//        composeRule.scrollToAndAssertDisplayed(
//            DiscoverMainContentColumn,
//            FeaturedChannelsErrorTag
//        )
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, DoNotMissItErrorTag)
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, PopularVideosErrorTag)
//    }
//
//    @Test
//    fun testLiveNowContent() {
//        every { mockHandler.state } returns MutableStateFlow(
//            DiscoverState(
//                liveNowLoading = false,
//                liveNowError = false,
//                liveNowVideos = listOf(createTestVideo(), createTestVideo(), createTestVideo())
//            )
//        )
//        composeRule.setContent {
//            RumbleTheme {
//                DiscoverScreen(
//                    discoverHandler = mockHandler
//                )
//            }
//        }
//
//
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, LiveNowRowTag)
//    }
//
//    @Test
//    fun testEditorPicksContent() {
//        every { mockHandler.state } returns MutableStateFlow(
//            DiscoverState(
//                editorPicksLoading = false,
//                editorPicksError = false,
//                editorPicks = listOf(createTestVideo(), createTestVideo(), createTestVideo())
//            )
//        )
//        composeRule.setContent {
//            RumbleTheme {
//                DiscoverScreen(
//                    discoverHandler = mockHandler
//                )
//            }
//        }
//
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, EditorPicksContentTag)
//    }
//
//    @Test
//    fun testDoNotMissItContent() {
//        every { mockHandler.state } returns MutableStateFlow(
//            DiscoverState(
//                doNotMissItLoading = false,
//                doNotMissItError = false,
//                doNotMissItVideo = createTestVideo()
//            )
//        )
//        composeRule.setContent {
//            RumbleTheme {
//                DiscoverScreen(
//                    discoverHandler = mockHandler,
//                )
//            }
//        }
//
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, DoNotMissItContentTag)
//    }
//
//    @Test
//    fun testPopularVideosContent() {
//        every { mockHandler.state } returns MutableStateFlow(
//            DiscoverState(
//                popularVideosLoading = false,
//                popularVideosError = false,
//                popularVideos = listOf(createTestVideo(), createTestVideo(), createTestVideo())
//            )
//        )
//        composeRule.setContent {
//            RumbleTheme {
//                DiscoverScreen(
//                    discoverHandler = mockHandler,
//                )
//            }
//        }
//
//        composeRule.scrollToAndAssertDisplayed(DiscoverMainContentColumn, PopularVideosContentTag)
//    }
//
//    private fun createTestVideo(): VideoEntity {
//        return mockk(relaxed = true)
//    }
//}