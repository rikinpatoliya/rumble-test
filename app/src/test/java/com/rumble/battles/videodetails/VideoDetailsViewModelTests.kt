package com.rumble.battles.videodetails

import android.app.Application
import android.content.pm.ActivityInfo
import androidx.lifecycle.SavedStateHandle
import com.rumble.battles.feed.presentation.videodetails.VideoDetailsViewModel
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogRumbleVideoUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoDetailsUseCase
import com.rumble.domain.analytics.domain.usecases.RumbleAdUpNextImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetUserCommentAuthorsUseCase
import com.rumble.domain.common.domain.usecase.AnnotatedStringUseCase
import com.rumble.domain.common.domain.usecase.OpenUriUseCase
import com.rumble.domain.common.domain.usecase.ShareUseCase
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.DeleteCommentUseCase
import com.rumble.domain.feed.domain.usecase.GetSensorBasedOrientationChangeEnabledUseCase
import com.rumble.domain.feed.domain.usecase.GetSingleRumbleAddUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoCommentsUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoDetailsUseCase
import com.rumble.domain.feed.domain.usecase.LikeCommentUseCase
import com.rumble.domain.feed.domain.usecase.MergeCommentsStateUserCase
import com.rumble.domain.feed.domain.usecase.PostCommentUseCase
import com.rumble.domain.feed.domain.usecase.ReportContentUseCase
import com.rumble.domain.feed.domain.usecase.UpdateCommentListReplyVisibilityUseCase
import com.rumble.domain.feed.domain.usecase.UpdateCommentVoteUseCase
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListVideosUseCase
import com.rumble.domain.livechat.domain.usecases.PostLiveChatMessageUseCase
import com.rumble.domain.livechat.domain.usecases.SendRantPurchasedEventUseCase
import com.rumble.domain.performance.domain.usecase.VideoLoadTimeTraceStartUseCase
import com.rumble.domain.performance.domain.usecase.VideoLoadTimeTraceStopUseCase
import com.rumble.domain.premium.domain.usecases.ShouldShowPremiumPromoUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.settings.domain.usecase.HasPremiumRestrictionUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.CreateRumblePlayListUseCase
import com.rumble.domain.video.domain.usecases.InitVideoPlayerSourceUseCase
import com.rumble.domain.video.domain.usecases.RequestEmailVerificationUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.domain.video.domain.usecases.UpdateVideoPlayerSourceUseCase
import com.rumble.network.session.SessionManager
import com.rumble.videoplayer.presentation.UiType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class VideoDetailsViewModelTests {

    private val testVideoId: Long = 100
    private val testChannelId: String = "101"
    private val savedState: SavedStateHandle = mockk(relaxed = true)
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase = mockk(relaxed = true)
    private val initVideoPlayerSourceUseCase: InitVideoPlayerSourceUseCase = mockk(relaxed = true)
    private val getChannelDataUseCase: GetChannelDataUseCase = mockk(relaxed = true)
    private val openUriUseCase: OpenUriUseCase = mockk(relaxed = true)
    private val annotatedStringUseCase: AnnotatedStringUseCase = mockk(relaxed = true)
    private val voteVideoUseCase: VoteVideoUseCase = mockk(relaxed = true)
    private val shareUseCase: ShareUseCase = mockk(relaxed = true)
    private val getSensorBasedOrientationChangeEnabledUseCase: GetSensorBasedOrientationChangeEnabledUseCase =
        mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private val videoEntity: VideoEntity = mockk(relaxed = true)
    private val channelDetailsEntity: ChannelDetailsEntity = mockk(relaxed = true)
    private val updateCommentVoteUseCase: UpdateCommentVoteUseCase = mockk(relaxed = true)
    private val postCommentUseCase: PostCommentUseCase = mockk(relaxed = true)
    private val deleteCommentUseCase: DeleteCommentUseCase = mockk(relaxed = true)
    private val mergeCommentsStateUserCase: MergeCommentsStateUserCase = mockk(relaxed = true)
    private val getVideoCommentsUseCase: GetVideoCommentsUseCase = mockk(relaxed = true)
    private val updateCommentListReplyVisibilityUseCase: UpdateCommentListReplyVisibilityUseCase =
        mockk(relaxed = true)
    private val likeCommentUseCase: LikeCommentUseCase = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val unhandledErrorUseCase: UnhandledErrorUseCase = mockk(relaxed = true)
    private val postLiveChatMessageUseCase: PostLiveChatMessageUseCase = mockk(relaxed = true)
    private val getRumbleAdUseCase: GetSingleRumbleAddUseCase = mockk(relaxed = true)
    private val adFeedImpressionUseCase: RumbleAdUpNextImpressionUseCase = mockk(relaxed = true)
    private val reportContentUseCase: ReportContentUseCase = mockk(relaxed = true)
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase = mockk(relaxed = true)
    private val requestEmailVerificationUseCase: RequestEmailVerificationUseCase =
        mockk(relaxed = true)
    private val getUserProfileUseCase: GetUserProfileUseCase = mockk(relaxed = true)
    private val saveLastPositionUseCase: SaveLastPositionUseCase = mockk(relaxed = true)
    private val logRumbleVideoUseCase: LogRumbleVideoUseCase = mockk(relaxed = true)
    private val logVideoDetailsUseCase: LogVideoDetailsUseCase = mockk(relaxed = true)
    private val analyticsEventUseCase: AnalyticsEventUseCase = mockk(relaxed = true)
    private val getUserCommentAuthorsUseCase: GetUserCommentAuthorsUseCase = mockk(relaxed = true)
    private val updateVideoPlayerSourceUseCase: UpdateVideoPlayerSourceUseCase = mockk(relaxed = true)
    private val userPreferenceManager: UserPreferenceManager = mockk(relaxed = true)
    private val createRumblePlayListUseCase: CreateRumblePlayListUseCase = mockk(relaxed = true)
    private val getPlayListUseCase: GetPlayListUseCase = mockk(relaxed = true)
    private val getPlayListVideosUseCase: GetPlayListVideosUseCase = mockk(relaxed = true)
    private val hasPremiumRestrictionUseCase: HasPremiumRestrictionUseCase = mockk(relaxed = true)
    private val sendRantPurchasedEventUseCase: SendRantPurchasedEventUseCase = mockk(relaxed = true)
    private val shouldShowPremiumPromoUseCase: ShouldShowPremiumPromoUseCase = mockk(relaxed = true)
    private val videoLoadTimeTraceStartUseCase: VideoLoadTimeTraceStartUseCase = mockk(relaxed = true)
    private val videoLoadTimeTraceStopUseCase: VideoLoadTimeTraceStopUseCase = mockk(relaxed = true)

    private lateinit var videoDetailsViewModel: VideoDetailsViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)

        every { videoEntity.channelId } returns testChannelId
        every { getSensorBasedOrientationChangeEnabledUseCase.invoke() } returns false
        coEvery { getVideoDetailsUseCase.invoke(testVideoId) } returns videoEntity
        coEvery {
            getChannelDataUseCase.invoke(testChannelId).getOrNull()
        } returns channelDetailsEntity

        videoDetailsViewModel = VideoDetailsViewModel(
            savedState = savedState,
            getVideoDetailsUseCase = getVideoDetailsUseCase,
            initVideoPlayerSourceUseCase = initVideoPlayerSourceUseCase,
            getChannelDataUseCase = getChannelDataUseCase,
            annotatedStringUseCase = annotatedStringUseCase,
            voteVideoUseCase = voteVideoUseCase,
            shareUseCase = shareUseCase,
            getVideoCommentsUseCase = getVideoCommentsUseCase,
            postCommentUseCase = postCommentUseCase,
            deleteCommentUseCase = deleteCommentUseCase,
            updateCommentListReplyVisibilityUseCase = updateCommentListReplyVisibilityUseCase,
            mergeCommentsStateUserCase = mergeCommentsStateUserCase,
            likeCommentUseCase = likeCommentUseCase,
            updateCommentVoteUseCase = updateCommentVoteUseCase,
            unhandledErrorUseCase = unhandledErrorUseCase,
            postLiveChatMessageUseCase = postLiveChatMessageUseCase,
            getSingleRumbleAddUseCase = getRumbleAdUseCase,
            rumbleAdUpNextImpressionUseCase = adFeedImpressionUseCase,
            reportContentUseCase = reportContentUseCase,
            getSensorBasedOrientationChangeEnabledUseCase = getSensorBasedOrientationChangeEnabledUseCase,
            application = application,
            sessionManager = sessionManager,
            logVideoCardImpressionUseCase = logVideoCardImpressionUseCase,
            logVideoDetailsUseCase = logVideoDetailsUseCase,
            requestEmailVerificationUseCase = requestEmailVerificationUseCase,
            getUserProfileUseCase = getUserProfileUseCase,
            saveLastPositionUseCase = saveLastPositionUseCase,
            logRumbleVideoUseCase = logRumbleVideoUseCase,
            analyticsEventUseCase = analyticsEventUseCase,
            getUserCommentAuthorsUseCase = getUserCommentAuthorsUseCase,
            updateVideoPlayerSourceUseCase = updateVideoPlayerSourceUseCase,
            userPreferenceManager = userPreferenceManager,
            createRumblePlayListUseCase = createRumblePlayListUseCase,
            getPlayListUseCase = getPlayListUseCase,
            getPlayListVideosUseCase = getPlayListVideosUseCase,
            hasPremiumRestrictionUseCase = hasPremiumRestrictionUseCase,
            sendRantPurchasedEventUseCase = sendRantPurchasedEventUseCase,
            shouldShowPremiumPromoUseCase = shouldShowPremiumPromoUseCase,
            videoLoadTimeTraceStartUseCase = videoLoadTimeTraceStartUseCase,
            videoLoadTimeTraceStopUseCase = videoLoadTimeTraceStopUseCase
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testOnFullScreen() {
        videoDetailsViewModel.onFullScreen(true, isTablet = false)
        assert(videoDetailsViewModel.state.value.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
        assert(videoDetailsViewModel.state.value.isFullScreen)
        assert(videoDetailsViewModel.state.value.uiType == UiType.FULL_SCREEN_LANDSCAPE)

        videoDetailsViewModel.onFullScreen(false, isTablet = false)
        assert(videoDetailsViewModel.state.value.screenOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        assert(videoDetailsViewModel.state.value.isFullScreen.not())
        assert(videoDetailsViewModel.state.value.uiType == UiType.EMBEDDED)
    }

    @Test
    fun testOnLike() {
        videoDetailsViewModel.onLike()
        coVerify { voteVideoUseCase.invoke(videoEntity, UserVote.LIKE) }
    }

    @Test
    fun testOnDislike() {
        videoDetailsViewModel.onDislike()
        coVerify { voteVideoUseCase.invoke(videoEntity, UserVote.DISLIKE) }
    }

    @Test
    fun testOnShare() {
        val testUri = "testUri"
        val testTitle = "testTag"
        every { videoEntity.url } returns testUri
        every { videoEntity.title } returns testTitle
        videoDetailsViewModel.onShare()
        verify { shareUseCase.invoke(testUri, testTitle) }
    }
}