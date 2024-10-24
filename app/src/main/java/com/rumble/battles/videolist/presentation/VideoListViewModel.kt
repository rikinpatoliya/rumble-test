package com.rumble.battles.videolist.presentation

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.analytics.CardSize
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.RumblePath
import com.rumble.domain.analytics.domain.domainmodel.videoListScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.domain.videolist.domain.model.VideoList
import com.rumble.domain.videolist.domain.usecase.GetVideoListUseCase
import com.rumble.videoplayer.player.RumblePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class VideoListState(
    val videoList: VideoList,
    val rumblePlayer: RumblePlayer? = null,
)

interface VideoListHandler: LazyListStateHandler {
    val videosPagingDataFlow: Flow<PagingData<Feed>>
    val state: StateFlow<VideoListState>
    val listToggleViewStyle: Flow<ListToggleViewStyle>
    val vmEvents: Flow<VideoListVmEvent>
    val updatedEntity: MutableStateFlow<VideoEntity?>
    val soundState: Flow<Boolean>
    val alertDialogState: State<AlertDialogState>

    fun onToggleVideoViewStyle(listToggleViewStyle: ListToggleViewStyle)
    fun onLike(videoEntity: VideoEntity)
    fun onDislike(videoEntity: VideoEntity)
    fun handleLoadState(loadStates: LoadStates)
    fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize)
    fun onPlayerImpression(videoEntity: VideoEntity)
    fun onFullyVisibleFeedChanged(feed: Feed?)
    fun onCreatePlayerForVisibleFeed()
    fun onSoundClick()
    fun onPauseCurrentPlayer()
    fun onViewResumed()
    fun onVideoClick(videoEntity: VideoEntity)
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity)
}

sealed class VideoListVmEvent {
    data class Error(val errorMessage: String? = null) : VideoListVmEvent()
    data class PlayVideo(val videoEntity: VideoEntity) : VideoListVmEvent()
}

sealed class VideoListAlertReason : AlertDialogReason {
    data class RestrictedContentReason(val videoEntity: VideoEntity) : VideoListAlertReason()
}

private const val TAG = "VideoListViewModel"

@HiltViewModel
class VideoListViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    getVideoListUseCase: GetVideoListUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase,
    private val getLastPositionUseCase: GetLastPositionUseCase,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
) : ViewModel(), VideoListHandler {

    override val state: MutableStateFlow<VideoListState> = MutableStateFlow(createInitialState())

    private val _vmEvents = Channel<VideoListVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<VideoListVmEvent> = _vmEvents.receiveAsFlow()

    override val updatedEntity: MutableStateFlow<VideoEntity?> = MutableStateFlow(null)

    override val soundState = userPreferenceManager.videoCardSoundStateFlow

    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())

    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    override fun updateListState(newState: LazyListState) {
        listState.value = newState
    }

    private val errorHandler = CoroutineExceptionHandler { _, error ->
        unhandledErrorUseCase(TAG, error)
        emitVmEvent(VideoListVmEvent.Error())
    }

    private var currentVisibleFeed: VideoEntity? = null
    private var lastDisplayedFeed: VideoEntity? = null

    init {
        observeSoundState()
    }

    override fun handleLoadState(loadStates: LoadStates) {
        arrayOf(
            loadStates.append,
            loadStates.prepend,
            loadStates.refresh
        ).filterIsInstance(LoadState.Error::class.java).firstOrNull()?.let { errorState ->
            unhandledErrorUseCase(TAG, errorState.error)
        }
    }

    override fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize) {
        viewModelScope.launch(errorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = videoEntity.videoLogView.view,
                screenId = videoListScreen,
                index = videoEntity.index,
                cardSize = cardSize
            )
        }
    }

    override fun onPlayerImpression(videoEntity: VideoEntity) {
        viewModelScope.launch {
            logVideoPlayerImpressionUseCase(
                screenId = videoListScreen,
                index = videoEntity.index,
                cardSize = CardSize.REGULAR
            )
        }
    }

    override val listToggleViewStyle: Flow<ListToggleViewStyle> =
        userPreferenceManager.videosListToggleViewStyle

    override val videosPagingDataFlow: Flow<PagingData<Feed>> =
        getVideoListUseCase(
            list = state.value.videoList
        ).cachedIn(viewModelScope)

    override fun onToggleVideoViewStyle(listToggleViewStyle: ListToggleViewStyle) {
        viewModelScope.launch(errorHandler) {
            userPreferenceManager.saveVideosListToggleViewStyle(listToggleViewStyle)
        }
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

    override fun onFullyVisibleFeedChanged(feed: Feed?) {
        if (feed is VideoEntity || feed == null) {
            currentVisibleFeed = feed as? VideoEntity
        }
    }

    override fun onCreatePlayerForVisibleFeed() {
        viewModelScope.launch {
            currentVisibleFeed?.let { video ->
                if (currentVisibleFeed?.id != lastDisplayedFeed?.id) {
                    state.value.rumblePlayer?.stopPlayer()
                    state.update { it.copy(rumblePlayer = initVideoCardPlayerUseCase(
                        videoEntity = video,
                        screenId = videoListScreen,
                        liveVideoReport = { _, result ->
                            if (result.hasLiveGate) {
                                viewModelScope.launch(Dispatchers.Main) {
                                    state.value.rumblePlayer?.stopPlayer()
                                    state.value = state.value.copy(rumblePlayer = null)
                                    lastDisplayedFeed = null
                                }
                            }
                        }
                    )) }
                    lastDisplayedFeed = currentVisibleFeed
                }
            }
        }
    }

    override fun onSoundClick() {
        viewModelScope.launch {
            userPreferenceManager.saveVideoCardSoundState(soundState.first().not())
        }
    }

    override fun onPauseCurrentPlayer() {
        state.value.rumblePlayer?.pauseVideo()
    }

    override fun onViewResumed() {
        viewModelScope.launch {
            currentVisibleFeed?.id?.let {
                state.value.rumblePlayer?.let { player ->
                    if (player.currentVideoAgeRestricted.not()) {
                        player.seekTo(getLastPositionUseCase(it))
                        player.playVideo()
                    }
                }
            }
        }
    }

    override fun onVideoClick(videoEntity: VideoEntity) {
        if (videoEntity.ageRestricted) {
            alertDialogState.value = AlertDialogState(
                true,
                VideoListAlertReason.RestrictedContentReason(videoEntity)
            )
        } else {
            savePosition(videoEntity)
            state.value.rumblePlayer?.stopPlayer()
            state.value = state.value.copy(rumblePlayer = null)
            lastDisplayedFeed = null
            emitVmEvent(VideoListVmEvent.PlayVideo(videoEntity))
        }
    }

    override fun onCancelRestricted() {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted(videoEntity: VideoEntity) {
        alertDialogState.value = AlertDialogState()
        savePosition(videoEntity)
        analyticsEventUseCase(MatureContentWatchEvent)
        emitVmEvent(VideoListVmEvent.PlayVideo(videoEntity))
    }

    private fun savePosition(videoEntity: VideoEntity) {
        state.value.rumblePlayer?.currentPositionValue?.let { lastPosition ->
            currentVisibleFeed?.id?.let { videoId ->
                if (videoEntity.id == videoId) {
                    viewModelScope.launch {
                        if (soundState.first()) saveLastPositionUseCase(lastPosition, videoId)
                    }
                }
            }
        }
    }

    private fun emitVmEvent(event: VideoListVmEvent) {
        _vmEvents.trySend(event)
    }

    private fun createInitialState() = VideoListState(
        videoList = VideoList.valueOf(
            stateHandle.get<String>(RumblePath.VIDEO_CATEGORY.path) ?: ""
        )
    )

    private fun observeSoundState() {
        viewModelScope.launch {
            userPreferenceManager.videoCardSoundStateFlow.collectLatest { enabled ->
                if (enabled.not()) state.value.rumblePlayer?.mute()
                else state.value.rumblePlayer?.unMute()
            }
        }
    }
}

