package com.rumble.battles.videos.presentation

import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.domain.usecases.CancelUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.DeleteUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.RestartUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.RestartWaitingConnectionVideoUploadsUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelVideosUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetUserUploadChannelsUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VoteResult
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.RequestEmailVerificationUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.session.SessionManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class MyVideosViewModelTest {

    private val sessionManager = mockk<SessionManager>(relaxed = true)
    private val getChannelDataUseCase = mockk<GetChannelDataUseCase>(relaxed = true)
    private val getChannelVideosUseCase = mockk<GetChannelVideosUseCase>(relaxed = true)
    private val voteVideoUseCase = mockk<VoteVideoUseCase>(relaxed = true)
    private val videoEntity = mockk<VideoEntity>(relaxed = true)
    private val userPreferenceManager: UserPreferenceManager = mockk(relaxed = true)
    private val unhandledErrorUseCase: UnhandledErrorUseCase = mockk(relaxed = true)
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase = mockk(relaxed = true)
    private val getUserUploadChannelsUseCase: GetUserUploadChannelsUseCase = mockk(relaxed = true)
    private val analyticsEventUseCase: AnalyticsEventUseCase = mockk(relaxed = true)
    private val getUploadVideoUseCase: GetUploadVideoUseCase = mockk(relaxed = true)
    private val cancelUploadVideoUseCase: CancelUploadVideoUseCase = mockk(relaxed = true)
    private val restartUploadVideoUseCase: RestartUploadVideoUseCase = mockk(relaxed = true)
    private val restartWaitingConnectionVideoUploadsUseCase: RestartWaitingConnectionVideoUploadsUseCase = mockk(relaxed = true)
    private val internetConnectionObserver: InternetConnectionObserver = mockk(relaxed = true)
    private val internetConnectionUseCase: InternetConnectionUseCase = mockk(relaxed = true)
    private val getLastPositionUseCase: GetLastPositionUseCase = mockk(relaxed = true)
    private val saveLastPositionUseCase: SaveLastPositionUseCase = mockk(relaxed = true)
    private val getUserProfileUseCase: GetUserProfileUseCase = mockk(relaxed = true)
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase = mockk(relaxed = true)
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase = mockk(relaxed = true)
    private val requestEmailVerificationUseCase: RequestEmailVerificationUseCase = mockk(relaxed = true)
    private val deleteUploadVideoUseCase: DeleteUploadVideoUseCase = mockk(relaxed = true)

    private lateinit var viewModel: MyVideosViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        coEvery {
            sessionManager.userIdFlow
        } returns flowOf("123")
        viewModel = MyVideosViewModel(
            sessionManager = sessionManager,
            getChannelDataUseCase = getChannelDataUseCase,
            getChannelVideosUseCase = getChannelVideosUseCase,
            voteVideoUseCase = voteVideoUseCase,
            userPreferenceManager = userPreferenceManager,
            unhandledErrorUseCase = unhandledErrorUseCase,
            logVideoCardImpressionUseCase = logVideoCardImpressionUseCase,
            getUserUploadChannelsUseCase = getUserUploadChannelsUseCase,
            analyticsEventUseCase = analyticsEventUseCase,
            getUploadVideoUseCase = getUploadVideoUseCase,
            cancelUploadVideoUseCase = cancelUploadVideoUseCase,
            restartUploadVideoUseCase = restartUploadVideoUseCase,
            restartWaitingConnectionVideoUploadsUseCase = restartWaitingConnectionVideoUploadsUseCase,
            internetConnectionObserver = internetConnectionObserver,
            internetConnectionUseCase = internetConnectionUseCase,
            getLastPositionUseCase = getLastPositionUseCase,
            saveLastPositionUseCase = saveLastPositionUseCase,
            getUserProfileUseCase = getUserProfileUseCase,
            initVideoCardPlayerUseCase = initVideoCardPlayerUseCase,
            logVideoPlayerImpressionUseCase = logVideoPlayerImpressionUseCase,
            requestEmailVerificationUseCase = requestEmailVerificationUseCase,
            deleteUploadVideoUseCase = deleteUploadVideoUseCase
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onLike() {
        coEvery { voteVideoUseCase.invoke(any(), UserVote.LIKE) } returns VoteResult(
            success = true,
            updatedFeed = videoEntity
        )
        viewModel.onLike(videoEntity)
        assert(viewModel.updatedEntity.value == videoEntity)
    }

    @Test
    fun onDislike() {
        coEvery { voteVideoUseCase.invoke(any(), UserVote.DISLIKE) } returns VoteResult(
            success = true,
            updatedFeed = videoEntity
        )
        viewModel.onDislike(videoEntity)
        assert(viewModel.updatedEntity.value == videoEntity)
    }
}