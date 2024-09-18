package com.rumble.battles.feed.presentation.feedlist

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.analytics.CardSize
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.R
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.domainmodel.feedScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.ProvideVideoReportConfigUseCase
import com.rumble.domain.analytics.domain.usecases.RumbleAdFeedImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FreshChannelListResult
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.common.domain.usecase.OpenUriUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdEntity
import com.rumble.domain.feed.domain.domainmodel.channel.FreshChannel
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionResult
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.GetFreshChannelsUseCase
import com.rumble.domain.feed.domain.usecase.GetHomeListUseCase
import com.rumble.domain.feed.domain.usecase.GetVideoCollectionsUseCase
import com.rumble.domain.feed.domain.usecase.GetViewCollectionTitleUseCase
import com.rumble.domain.feed.domain.usecase.SaveVideoCollectionViewUseCase
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.report.domain.VideoReportConfig
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import com.rumble.network.session.SessionManager
import com.rumble.videoplayer.player.RumblePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

enum class LoadingState {
    None, Loading, Error, Done
}

data class HomeScreenState(
    val selectedCollection: VideoCollectionType? = null,
    val videoList: Flow<PagingData<Feed>> = emptyFlow(),
    val connectionState: InternetConnectionState = InternetConnectionState.CONNECTED,
    val freshChannels: List<FreshChannel> = emptyList(),
    val freshContentLoadingState: LoadingState = LoadingState.None,
)

sealed class HomeAlertReason : AlertDialogReason {
    data class RestrictedContentReason(val videoEntity: VideoEntity) : HomeAlertReason()
}

sealed class HomeEvent {
    data class PlayVideo(val videoEntity: VideoEntity) : HomeEvent()
    data class NavigateToChannelDetails(val channelId: String) : HomeEvent()
}

interface HomeHandler {
    val homeScreenState: StateFlow<HomeScreenState>
    val homeCategories: StateFlow<List<VideoCollectionType>>
    val updatedEntity: StateFlow<VideoEntity?>
    val currentPlayerState: State<RumblePlayer?>
    val soundState: Flow<Boolean>
    val reportConfig: VideoReportConfig
    val alertDialogState: State<AlertDialogState>
    val eventFlow: Flow<HomeEvent>

    fun onRefreshAll()
    fun onLike(videoEntity: VideoEntity)
    fun onDislike(videoEntity: VideoEntity)
    fun onRumbleAdClick(rumbleAd: RumbleAdEntity)
    fun onRumbleAdImpression(rumbleAd: RumbleAdEntity)
    fun onVideoCardImpression(videoEntity: VideoEntity)
    fun onPlayerImpression(videoEntity: VideoEntity)
    fun onVideoCollectionClick(videoCollection: VideoCollectionType)
    fun onFullyVisibleFeedChanged(feed: Feed?)
    fun onCreatePlayerForVisibleFeed()
    fun onPauseCurrentPlayer()
    fun onSoundClick()
    fun onVideoClick(feed: Feed)
    fun onViewResumed()
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity)
    fun onRumbleAdResumed(rumbleAd: RumbleAdEntity)
    fun onDismissPremiumBanner()
}

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val sessionManager: SessionManager,
    private val getHomeListUseCase: GetHomeListUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val openUriUseCase: OpenUriUseCase,
    private val adFeedImpressionUseCase: RumbleAdFeedImpressionUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val getFreshChannelsUseCase: GetFreshChannelsUseCase,
    private val getVideoCollectionsUseCase: GetVideoCollectionsUseCase,
    private val saveVideoCollectionViewUseCase: SaveVideoCollectionViewUseCase,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val getLastPositionUseCase: GetLastPositionUseCase,
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase,
    private val provideVideoReportConfigUseCase: ProvideVideoReportConfigUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase,
    private val getViewCollectionTitleUseCase: GetViewCollectionTitleUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
    private val internetConnectionUseCase: InternetConnectionUseCase,
) : AndroidViewModel(application), HomeHandler {

    override val homeScreenState: MutableStateFlow<HomeScreenState> = MutableStateFlow(
        HomeScreenState()
    )

    override val homeCategories: MutableStateFlow<List<VideoCollectionType>> =
        MutableStateFlow(emptyList())

    override val updatedEntity: MutableStateFlow<VideoEntity?> = MutableStateFlow(null)

    override val currentPlayerState: MutableState<RumblePlayer?> = mutableStateOf(null)

    override val soundState = userPreferenceManager.videoCardSoundStateFlow

    override val reportConfig: VideoReportConfig
        get() = provideVideoReportConfigUseCase()

    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())

    override val eventFlow: MutableSharedFlow<HomeEvent> = MutableSharedFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    private var currentVisibleFeed: VideoEntity? = null
    private var lastDisplayedFeed: VideoEntity? = null
    private var isPremium: Boolean? = null

    init {
        observeUserAuthState()
        observeUserData()
        startObserveConnectionState()
        observeSoundState()
        observePlaybackInFeedMode()
        observeIfContentLoadAllowed()
    }

    override fun onLike(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.LIKE)
            if (result.success) updatedEntity.value = result.updatedFeed
        }
    }

    override fun onDislike(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.DISLIKE)
            if (result.success) updatedEntity.value = result.updatedFeed
        }
    }

    override fun onRumbleAdClick(rumbleAd: RumbleAdEntity) = openUriUseCase(TAG, rumbleAd.clickUrl)

    override fun onRumbleAdImpression(rumbleAd: RumbleAdEntity) {
        viewModelScope.launch(errorHandler) {
            adFeedImpressionUseCase(rumbleAd)
        }
        insureAdIsFresh(rumbleAd)
    }

    override fun onVideoCardImpression(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            homeScreenState.value.selectedCollection?.let {
                logVideoCardImpressionUseCase(
                    videoPath = videoEntity.videoLogView.view,
                    screenId = feedScreen,
                    index = videoEntity.index,
                    cardSize = CardSize.REGULAR,
                    category = getViewCollectionTitleUseCase(
                        viewCollectionType = it,
                        defaultTitle = getApplication<Application>().getString(R.string.home_category_my_feed)
                    )
                )
            }
        }
    }

    override fun onPlayerImpression(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            homeScreenState.value.selectedCollection?.let {
                logVideoPlayerImpressionUseCase(
                    screenId = feedScreen,
                    index = videoEntity.index,
                    cardSize = CardSize.REGULAR,
                    category = getViewCollectionTitleUseCase(
                        viewCollectionType = it,
                        defaultTitle = getApplication<Application>().getString(R.string.home_category_my_feed)
                    )
                )
            }
        }
    }

    override fun onVideoCollectionClick(videoCollection: VideoCollectionType) {
        if (videoCollection != homeScreenState.value.selectedCollection) {
            currentPlayerState.value?.pauseVideo()
            viewModelScope.launch(errorHandler) {
                saveVideoCollectionViewUseCase.invoke(
                    collectionType = videoCollection
                )
            }

            homeScreenState.value = homeScreenState.value.copy(
                selectedCollection = videoCollection,
                videoList = getHomeListUseCase(
                    videoCollection,
                    getViewCollectionTitleUseCase(
                        viewCollectionType = videoCollection,
                        defaultTitle = getApplication<Application>().getString(R.string.home_category_my_feed)
                    )
                ).cachedIn(viewModelScope)
            )
        }
    }

    override fun onRefreshAll() {
        currentPlayerState.value?.pauseVideo()
        loadChannelsWithFreshContent()
        loadVideoCollections()
        onRefreshOnlyVideoList()
    }

    override fun onFullyVisibleFeedChanged(feed: Feed?) {
        if (feed is VideoEntity || feed == null) {
            currentVisibleFeed = feed as? VideoEntity
        }
    }

    override fun onCreatePlayerForVisibleFeed() {
        viewModelScope.launch {
            currentVisibleFeed?.let {
                if (currentVisibleFeed?.id != lastDisplayedFeed?.id) {
                    currentPlayerState.value?.stopPlayer()
                    currentPlayerState.value = initVideoCardPlayerUseCase(it, feedScreen)
                    lastDisplayedFeed = currentVisibleFeed
                }
            }
        }
    }

    override fun onPauseCurrentPlayer() {
        currentPlayerState.value?.pauseVideo()
    }

    override fun onSoundClick() {
        viewModelScope.launch {
            userPreferenceManager.saveVideoCardSoundState(soundState.first().not())
        }
    }

    override fun onVideoClick(feed: Feed) {
        (feed as? VideoEntity)?.let { videoEntity ->
            if (videoEntity.ageRestricted) {
                alertDialogState.value = AlertDialogState(
                    true,
                    HomeAlertReason.RestrictedContentReason(videoEntity)
                )
            } else {
                currentPlayerState.value?.currentPositionValue?.let { position ->
                    currentVisibleFeed?.id?.let { videoId ->
                        if (videoEntity.id == videoId) saveLastPosition(position, videoId)
                    }
                }
                emitVmEvent(HomeEvent.PlayVideo(videoEntity))
            }
        }
    }

    override fun onViewResumed() {
        viewModelScope.launch {
            currentVisibleFeed?.id?.let {
                currentPlayerState.value?.let { player ->
                    if (player.currentVideoAgeRestricted.not()) {
                        player.playVideo()
                        player.seekTo(getLastPositionUseCase(it))
                    }
                }
            }
        }
    }

    override fun onCancelRestricted() {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted(videoEntity: VideoEntity) {
        alertDialogState.value = AlertDialogState()
        currentPlayerState.value?.currentPositionValue?.let { position ->
            currentVisibleFeed?.id?.let { videoId ->
                if (videoEntity.id == videoId) saveLastPosition(position, videoId)
            }
        }
        analyticsEventUseCase(MatureContentWatchEvent)
        emitVmEvent(HomeEvent.PlayVideo(videoEntity))
    }

    override fun onRumbleAdResumed(rumbleAd: RumbleAdEntity) =
        insureAdIsFresh(rumbleAd)

    override fun onDismissPremiumBanner() {
        viewModelScope.launch {
            userPreferenceManager.setDisplayPremiumBanner(false)
        }
    }

    private fun observeUserAuthState() {
        viewModelScope.launch {
            sessionManager.cookiesFlow.distinctUntilChanged().collectLatest { cookies ->
                if (cookies.isNotBlank()) {
                    loadChannelsWithFreshContent()
                    loadVideoCollections()
                }
            }
        }
    }

    private fun observeIfContentLoadAllowed() {
        viewModelScope.launch {
            sessionManager.allowContentLoadFlow.distinctUntilChanged().collectLatest { allowed ->
                if (allowed) {
                    initPremiumStatus()
                    loadChannelsWithFreshContent()
                    loadVideoCollections()
                }
            }
        }
    }

    private fun insureAdIsFresh(rumbleAd: RumbleAdEntity) {
        if (rumbleAd.expirationLocal.isBefore(LocalDateTime.now())) {
            onRefreshOnlyVideoList()
        }
    }

    private fun saveLastPosition(lastPosition: Long, videoId: Long) {
        viewModelScope.launch {
            if (soundState.first()) saveLastPositionUseCase(lastPosition, videoId)
        }
    }

    private fun observeSoundState() {
        viewModelScope.launch {
            userPreferenceManager.videoCardSoundStateFlow.collectLatest { enabled ->
                if (enabled.not()) currentPlayerState.value?.mute()
                else currentPlayerState.value?.unMute()
            }
        }
    }

    private fun observePlaybackInFeedMode() {
        viewModelScope.launch {
            userPreferenceManager.playbackInFeedsModeModeFlow.distinctUntilChanged().collectLatest {
                lastDisplayedFeed = null
                onCreatePlayerForVisibleFeed()
            }
        }
    }

    private fun startObserveConnectionState() {
        viewModelScope.launch(errorHandler) {
            homeScreenState.update {
                it.copy(connectionState = internetConnectionUseCase())
            }
            internetConnectionObserver.connectivityFlow.collectLatest {
                if ((it == InternetConnectionState.CONNECTED) and (homeScreenState.value.connectionState == InternetConnectionState.LOST)) {
                    onRefreshAll()
                }
                homeScreenState.update { uiState ->
                    uiState.copy(connectionState = it)
                }
            }
        }
    }

    private fun onRefreshOnlyVideoList() {
        homeScreenState.value.selectedCollection?.let {
            homeScreenState.value = homeScreenState.value.copy(
                videoList = getHomeListUseCase(
                    it,
                    getViewCollectionTitleUseCase(
                        it,
                        getApplication<Application>().getString(R.string.home_category_my_feed)
                    )
                ).cachedIn(viewModelScope)
            )
        }
    }

    private fun initPremiumStatus() {
        viewModelScope.launch {
            isPremium = sessionManager.isPremiumUserFlow.first()
        }
    }

    private fun observeUserData() {
        viewModelScope.launch {
            sessionManager.isPremiumUserFlow.collect {
                if (it != isPremium && isPremium != null) {
                    onRefreshAll()
                    isPremium = it
                }
            }
        }
    }

    private fun loadChannelsWithFreshContent() {
        viewModelScope.launch(errorHandler) {
            if (sessionManager.isUserSignedIn()) {
                homeScreenState.value = homeScreenState.value.copy(
                    freshContentLoadingState = LoadingState.Loading
                )

                when (val result = getFreshChannelsUseCase()) {
                    is FreshChannelListResult.Failure -> {
                        homeScreenState.value = homeScreenState.value.copy(
                            freshChannels = emptyList(),
                            freshContentLoadingState = LoadingState.Error
                        )
                    }

                    is FreshChannelListResult.Success -> {
                        homeScreenState.value = homeScreenState.value.copy(
                            freshChannels = result.channels,
                            freshContentLoadingState = LoadingState.Done
                        )
                    }
                }
            }
        }
    }

    private fun loadVideoCollections() {
        viewModelScope.launch(errorHandler) {
            when (val result = getVideoCollectionsUseCase()) {
                is VideoCollectionResult.Failure -> homeCategories.value = emptyList()
                is VideoCollectionResult.Success -> {
                    if (homeScreenState.value.selectedCollection == null && result.videoCollections.isNotEmpty()) {
                        homeScreenState.value = homeScreenState.value.copy(
                            videoList = getHomeListUseCase(
                                result.videoCollections[0],
                                getViewCollectionTitleUseCase(
                                    result.videoCollections[0],
                                    getApplication<Application>().getString(R.string.home_category_my_feed)
                                )
                            ).cachedIn(
                                viewModelScope
                            ),
                            selectedCollection = result.videoCollections[0]
                        )
                    }
                    homeCategories.value = result.videoCollections
                }
            }
        }
    }

    private fun emitVmEvent(event: HomeEvent) =
        viewModelScope.launch { eventFlow.emit(event) }
}