//package com.rumble.battles.videodetails
//
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.lazy.LazyListState
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.test.assertIsDisplayed
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithTag
//import androidx.compose.ui.test.performClick
//import com.rumble.battles.*
//import com.rumble.battles.feed.presentation.videodetails.VideoDetailsHandler
//import com.rumble.battles.feed.presentation.videodetails.VideoDetailsState
//import com.rumble.battles.feed.presentation.videodetails.VideoDetailsView
//import com.rumble.battles.livechat.presentation.LiveChatHandler
//import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
//import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
//import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
//import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
//import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
//import com.rumble.theme.RumbleTheme
//import io.mockk.every
//import io.mockk.mockk
//import io.mockk.verify
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//@OptIn(ExperimentalMaterialApi::class)
//class VideoDetailsContentTests {
//
//    @get:Rule
//    val composeRule = createComposeRule()
//
//    private val mockHandler = mockk<VideoDetailsHandler>(relaxed = true)
//    private val mockState = mockk<VideoDetailsState>(relaxed = true)
//    private val mockVideo = mockk<VideoEntity>(relaxed = true)
//    private val mockChannelDetails = mockk<ChannelDetailsEntity>(relaxed = true)
//    private val mockLocals = mockk<LocalsCommunityEntity>(relaxed = true)
//    private val mockFollowStatus = mockk<FollowStatus>(relaxed = true)
//    private val mockLiveChatHandler: LiveChatHandler = mockk(relaxed = true)
//    private val mockListState: LazyListState = mockk(relaxed = true)
//
//    @Before
//    fun setup() {
//        every { mockHandler.state } returns mutableStateOf(mockState)
//        every { mockState.videoEntity } returns mockVideo
//        every { mockState.channelDetailsEntity } returns mockChannelDetails
//        every { mockState.rumblePlayer } returns null
//        every { mockState.isLoading } returns false
//        every { mockState.followStatus } returns mockFollowStatus
//    }
//
//    @Test
//    fun testLoadingIsVisible() {
//        every { mockState.isLoading } returns true
//        setContentView()
//        composeRule.onNodeWithTag(JoinOnLocalsViewTag).assertIsDisplayed()
//    }
//
//    @Test
//    fun testLocalsNotVisible() {
//        every { mockChannelDetails.localsCommunityEntity } returns null
//        setContentView()
//        composeRule.onNodeWithTag(JoinOnLocalsViewTag).assertDoesNotExist()
//    }
//
//    @Test
//    fun testLocalsVisible() {
//        every { mockChannelDetails.localsCommunityEntity } returns mockLocals
//        setContentView()
//        composeRule.onNodeWithTag(JoinOnLocalsViewTag).assertIsDisplayed()
//    }
//
//    @Test
//    fun testShareButtonClick() {
//        setContentView()
//        composeRule.onNodeWithTag(ShareButtonTag).performClick()
//        verify { mockHandler.onShare() }
//    }
//
//    @Test
//    fun testFollowButtonClick() {
//        every { mockFollowStatus.updateAction } returns { UpdateChannelSubscriptionAction.UNSUBSCRIBE }
//        setContentView()
//        composeRule.onNodeWithTag(ChannelDetailsActionButtonsTag).performClick()
//        verify { mockHandler.onUpdateSubscription(UpdateChannelSubscriptionAction.UNSUBSCRIBE) }
//    }
//
//    @Test
//    fun testLikeButtonClick() {
//        setContentView()
//        composeRule.onNodeWithTag(LikeVideTag).performClick()
//        verify { mockHandler.onLike() }
//    }
//
//    @Test
//    fun testDislikeClick() {
//        setContentView()
//        composeRule.onNodeWithTag(DislikeVideTag).performClick()
//        verify { mockHandler.onDislike() }
//    }
//
//    private fun setContentView() {
//        composeRule.setContent {
//            RumbleTheme {
//                VideoDetailsView(
//                    modifier = Modifier.fillMaxSize(),
//                    handler = mockHandler,
//                    liveChatHandler = mockLiveChatHandler,
//                    listState = mockListState,
//                    onVideoClick = {},
//                    onChannelClick = {}
//                )
//            }
//        }
//    }
//}