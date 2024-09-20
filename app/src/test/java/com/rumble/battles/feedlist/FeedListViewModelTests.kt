package com.rumble.battles.feedlist

import android.app.Application
import com.rumble.battles.feed.presentation.feedlist.HomeViewModel
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.RumbleAdFeedImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelSubscriptionUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.common.domain.usecase.OpenUriUseCase
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VoteResult
import com.rumble.domain.feed.domain.usecase.GetFreshChannelsUseCase
import com.rumble.domain.feed.domain.usecase.GetHomeListUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoCollectionsUseCase
import com.rumble.domain.feed.domain.usecase.GetViewCollectionTitleUseCase
import com.rumble.domain.feed.domain.usecase.SaveVideoCollectionViewUseCase
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.session.SessionManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class FeedListViewModelTests {

    private val getHomeListUseCase = mockk<GetHomeListUseCase>(relaxed = true)
    private val voteVideoUseCase = mockk<VoteVideoUseCase>(relaxed = true)
    private val videoEntity = mockk<VideoEntity>(relaxed = true)
    private val unhandledErrorUseCase: UnhandledErrorUseCase = mockk(relaxed = true)
    private val openUriUseCase: OpenUriUseCase = mockk(relaxed = true)
    private val adFeedImpressionUseCase: RumbleAdFeedImpressionUseCase = mockk(relaxed = true)
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase = mockk(relaxed = true)
    private val getFreshChannelsUseCase: GetFreshChannelsUseCase = mockk(relaxed = true)
    private val getHomeCategoriesUserCase: GetVideoCollectionsUseCase = mockk(relaxed = true)
    private val updateHomeCategoriesUseCase: SaveVideoCollectionViewUseCase = mockk(relaxed = true)
    private val analyticsEventUseCase: AnalyticsEventUseCase = mockk(relaxed = true)
    private val internetConnectionObserver: InternetConnectionObserver = mockk(relaxed = true)
    private val internetConnectionUseCase: InternetConnectionUseCase = mockk(relaxed = true)
    private val application: Application = mockk(relaxed = true)
    private val getLastPositionUseCase: GetLastPositionUseCase = mockk(relaxed = true)
    private val getViewCollectionTitleUseCase: GetViewCollectionTitleUseCase = mockk(relaxed = true)
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase = mockk(relaxed = true)
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase = mockk(relaxed = true)
    private val saveLastPositionUseCase: SaveLastPositionUseCase = mockk(relaxed = true)
    private val userPreferenceManager: UserPreferenceManager = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val updateChannelSubscriptionUseCase: UpdateChannelSubscriptionUseCase = mockk(relaxed = true)

    private lateinit var viewModel: HomeViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = HomeViewModel(
            application = application,
            getHomeListUseCase = getHomeListUseCase,
            voteVideoUseCase = voteVideoUseCase,
            unhandledErrorUseCase = unhandledErrorUseCase,
            openUriUseCase = openUriUseCase,
            adFeedImpressionUseCase = adFeedImpressionUseCase,
            logVideoCardImpressionUseCase = logVideoCardImpressionUseCase,
            getFreshChannelsUseCase = getFreshChannelsUseCase,
            getVideoCollectionsUseCase = getHomeCategoriesUserCase,
            saveVideoCollectionViewUseCase = updateHomeCategoriesUseCase,
            analyticsEventUseCase = analyticsEventUseCase,
            internetConnectionObserver = internetConnectionObserver,
            internetConnectionUseCase = internetConnectionUseCase,
            getLastPositionUseCase = getLastPositionUseCase,
            getViewCollectionTitleUseCase = getViewCollectionTitleUseCase,
            initVideoCardPlayerUseCase = initVideoCardPlayerUseCase,
            logVideoPlayerImpressionUseCase = logVideoPlayerImpressionUseCase,
            saveLastPositionUseCase = saveLastPositionUseCase,
            userPreferenceManager = userPreferenceManager,
            sessionManager = sessionManager,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testOnLike() {
        coEvery { voteVideoUseCase.invoke(any(), UserVote.LIKE) } returns VoteResult(
            success = true,
            updatedFeed = videoEntity
        )
        viewModel.onLike(videoEntity)
        assert(viewModel.updatedEntity.value == videoEntity)
    }

    @Test
    fun testOnDislike() {
        coEvery { voteVideoUseCase.invoke(any(), UserVote.DISLIKE) } returns VoteResult(
            success = true,
            updatedFeed = videoEntity
        )
        viewModel.onDislike(videoEntity)
        assert(viewModel.updatedEntity.value == videoEntity)
    }
}