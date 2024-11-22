package com.rumble.battles.channels.channeldetails.presentation

import androidx.lifecycle.SavedStateHandle
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelVideosUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.LogChannelViewUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.ReportChannelUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsData
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelSubscriptionUseCase
import com.rumble.domain.common.domain.usecase.SendEmailUseCase
import com.rumble.domain.common.domain.usecase.ShareUseCase
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VoteResult
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.sort.NotificationFrequency
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.network.session.SessionManager
import com.rumble.videoplayer.player.config.ReportType
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class CreatorDetailsViewModelTest {

    private val getChannelDataUseCase = mockk<GetChannelDataUseCase>(relaxed = true)
    private val getChannelVideosUseCase = mockk<GetChannelVideosUseCase>(relaxed = true)
    private val updateChannelSubscriptionUseCase =
        mockk<UpdateChannelSubscriptionUseCase>(relaxed = true)
    private val updateNotificationsUseCase =
        mockk<UpdateChannelNotificationsUseCase>(relaxed = true)
    private val voteVideoUseCase = mockk<VoteVideoUseCase>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    private val videoEntity = mockk<VideoEntity>(relaxed = true)
    private val localsCommunityEntity = mockk<LocalsCommunityEntity>(relaxed = true)
    private val channelDetailsEntity = mockk<CreatorEntity>(relaxed = true)
    private val updateChannelNotificationsData =
        mockk<UpdateChannelNotificationsData>(relaxed = true)
    private val notificationFrequency = mockk<NotificationFrequency>(relaxed = true)
    private val sendEmailUseCase = mockk<SendEmailUseCase>(relaxed = true)
    private val logChannelViewUseCase = mockk<LogChannelViewUseCase>(relaxed = true)
    private val userPreferenceManager: UserPreferenceManager = mockk(relaxed = true)
    private val unhandledErrorUseCase: UnhandledErrorUseCase = mockk(relaxed = true)
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase = mockk(relaxed = true)
    private val reportChannelUseCase: ReportChannelUseCase = mockk(relaxed = true)
    private val analyticsEventUseCase: AnalyticsEventUseCase = mockk(relaxed = true)
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase = mockk(relaxed = true)
    private val getLastPositionUseCase: GetLastPositionUseCase = mockk(relaxed = true)
    private val saveLastPositionUseCase: SaveLastPositionUseCase = mockk(relaxed = true)
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase = mockk(relaxed = true)
    private val shareUseCase: ShareUseCase = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)

    private lateinit var viewModel: ChannelDetailsViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        coEvery { savedStateHandle.get<String>(any()) } returns "123"
        viewModel = ChannelDetailsViewModel(
            getChannelDataUseCase = getChannelDataUseCase,
            logChannelViewUseCase = logChannelViewUseCase,
            getChannelVideosUseCase = getChannelVideosUseCase,
            voteVideoUseCase = voteVideoUseCase,
            stateHandle = savedStateHandle,
            userPreferenceManager = userPreferenceManager,
            unhandledErrorUseCase = unhandledErrorUseCase,
            logVideoCardImpressionUseCase = logVideoCardImpressionUseCase,
            reportChannelUseCase = reportChannelUseCase,
            analyticsEventUseCase = analyticsEventUseCase,
            initVideoCardPlayerUseCase = initVideoCardPlayerUseCase,
            getLastPositionUseCase = getLastPositionUseCase,
            saveLastPositionUseCase = saveLastPositionUseCase,
            logVideoPlayerImpressionUseCase = logVideoPlayerImpressionUseCase,
            shareUseCase = shareUseCase,
            sessionManager = sessionManager,
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

    @Test
    fun onJoin() {
        viewModel.onJoin(localsCommunityEntity)
        runBlocking {
            assert(viewModel.vmEvents.first() == ChannelDetailsVmEvent.ShowLocalsPopup)
        }
    }

    @Test
    fun onActionMenuClicked() =
        runBlocking {
            viewModel.onActionMenuClicked()
            assert(viewModel.popupState.value == ChannelDetailsDialog.ActionMenuDialog)
            assert(viewModel.vmEvents.first() == ChannelDetailsVmEvent.ShowMenuPopup)
        }

    @Test
    fun onBlockMenuClicked() = runBlocking {
        viewModel.onBlockMenuClicked()
        assert(viewModel.popupState.value == ChannelDetailsDialog.BlockDialog(channelDetailsEntity))
        assert(viewModel.vmEvents.first() == ChannelDetailsVmEvent.ShowMenuPopup)
    }

    @Test
    fun onReportMenuClicked() = runBlocking {
        viewModel.onReportMenuClicked()
        assert(viewModel.popupState.value == ChannelDetailsDialog.ReportDialog)
        assert(viewModel.vmEvents.first() == ChannelDetailsVmEvent.ShowMenuPopup)
    }

    @Test
    fun onReport() = runBlocking {
        viewModel.onReport(ReportType.SPAM)
        verify { sendEmailUseCase.invoke(any(), any(), any()) }
    }
}