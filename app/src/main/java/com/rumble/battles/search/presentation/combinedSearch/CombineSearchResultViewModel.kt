package com.rumble.battles.search.presentation.combinedSearch

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.analytics.CardSize
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.RumblePath
import com.rumble.battles.sort.SortFilterSelection
import com.rumble.domain.analytics.domain.domainmodel.combineSearchScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.search.domain.useCases.SearchCombineUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.sort.DurationType
import com.rumble.domain.sort.FilterType
import com.rumble.domain.sort.SortType
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.utils.extension.navigationSafeDecode
import com.rumble.videoplayer.player.RumblePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

interface CombineSearchResultHandler: LazyListStateHandler {
    val query: String
    val state: State<CombineSearchResultState>
    val alertDialogState: State<AlertDialogState>
    val selection: SortFilterSelection
    val soundState: Flow<Boolean>
    val eventFlow: Flow<CombinedSearchEvent>

    fun onSelectionMade(newSelection: SortFilterSelection)
    fun onLike(video: VideoEntity)
    fun onDislike(video: VideoEntity)
    fun onVideoCardImpression(video: VideoEntity)
    fun onFullyVisibleFeedChanged(video: VideoEntity?)
    fun onCreatePlayerForVisibleFeed()
    fun onSoundClick()
    fun onDisposed()
    fun onViewResumed()
    fun onPlayerImpression(videoEntity: VideoEntity)
    fun onVideoItemClick(videoEntity: VideoEntity)
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity)
}

enum class SearchState {
    LOADING,
    EMPTY,
    EMPTY_CHANNELS,
    EMPTY_VIDEOS,
    LOADED
}

data class CombineSearchResultState(
    val timeStamp: Long = System.currentTimeMillis(),
    val searchState: SearchState = SearchState.LOADING,
    val channelList: List<ChannelDetailsEntity> = emptyList(),
    val videoList: List<VideoEntity> = emptyList(),
    val rumblePlayer: RumblePlayer? = null,
)

sealed class CombinedSearchEvent {
    data class PlayVideo(val videoEntity: VideoEntity) : CombinedSearchEvent()
}

sealed class CombinedSearchAlertReason : AlertDialogReason {
    data class RestrictedContentReason(val videoEntity: VideoEntity) : CombinedSearchAlertReason()
}

private const val TAG = "CombineSearchResultViewModel"

@HiltViewModel
class CombineSearchResultViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val searchCombineUseCase: SearchCombineUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val getLastPositionUseCase: GetLastPositionUseCase,
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
) : ViewModel(), CombineSearchResultHandler {

    private val contentErrorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        state.value = CombineSearchResultState(searchState = SearchState.EMPTY)
    }
    private val updateErrorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    override var selection: SortFilterSelection = SortFilterSelection(
        sortSelection = SortType.values().first(),
        filterSelection = FilterType.values().first(),
        durationSelection = DurationType.values().first()
    )
    override val query: String =
        (stateHandle.get<String>(RumblePath.QUERY.path) ?: "").navigationSafeDecode()
    override val state: MutableState<CombineSearchResultState> =
        mutableStateOf(CombineSearchResultState(searchState = SearchState.LOADING))
    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())
    override val soundState = userPreferenceManager.videoCardSoundStateFlow
    override val eventFlow: MutableSharedFlow<CombinedSearchEvent> = MutableSharedFlow()

    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    override fun updateListState(newState: LazyListState) {
        listState.value = newState
    }

    private var currentVisibleFeed: VideoEntity? = null
    private var lastDisplayedFeed: VideoEntity? = null

    init {
        viewModelScope.launch(contentErrorHandler) {
            fetchData(selection)
        }
        observeSoundState()
    }

    override fun onLike(video: VideoEntity) {
        viewModelScope.launch(updateErrorHandler) {
            val result = voteVideoUseCase(video, UserVote.LIKE)
            if (result.success) updateState(result.updatedFeed)
        }
    }

    override fun onDislike(video: VideoEntity) {
        viewModelScope.launch(updateErrorHandler) {
            val result = voteVideoUseCase(video, UserVote.DISLIKE)
            if (result.success) updateState(result.updatedFeed)
        }
    }

    override fun onSelectionMade(newSelection: SortFilterSelection) {
        selection = newSelection
        viewModelScope.launch(contentErrorHandler) {
            fetchData(selection)
        }
    }

    override fun onVideoCardImpression(video: VideoEntity) {
        viewModelScope.launch(contentErrorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = video.videoLogView.view,
                screenId = combineSearchScreen,
                index = video.index,
                cardSize = CardSize.REGULAR
            )
        }
    }

    override fun onFullyVisibleFeedChanged(video: VideoEntity?) {
        currentVisibleFeed = video
    }

    override fun onCreatePlayerForVisibleFeed() {
        viewModelScope.launch {
            currentVisibleFeed?.let {
                if (currentVisibleFeed?.id != lastDisplayedFeed?.id) {
                    state.value.rumblePlayer?.stopPlayer()
                    state.value = state.value.copy(rumblePlayer = initVideoCardPlayerUseCase(it, combineSearchScreen))
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

    override fun onDisposed() {
        state.value.rumblePlayer?.let {
            it.pauseVideo()
            viewModelScope.launch {
                if (userPreferenceManager.videoCardSoundStateFlow.first()) {
                    saveLastPositionUseCase(it.currentPositionValue, it.videoId)
                }
            }
        }
    }

    override fun onViewResumed() {
        viewModelScope.launch {
            currentVisibleFeed?.id?.let {
                state.value.rumblePlayer?.let { player ->
                    if (player.currentVideoAgeRestricted.not()) {
                        player.playVideo()
                        player.seekTo(getLastPositionUseCase(it))
                    }
                }
            }
        }
    }

    override fun onPlayerImpression(videoEntity: VideoEntity) {
        viewModelScope.launch {
            logVideoPlayerImpressionUseCase(
                screenId = combineSearchScreen,
                index = videoEntity.index,
                cardSize = CardSize.REGULAR
            )
        }
    }

    override fun onVideoItemClick(videoEntity: VideoEntity) {
        if (videoEntity.ageRestricted) {
            alertDialogState.value = AlertDialogState(
                true,
                CombinedSearchAlertReason.RestrictedContentReason(videoEntity)
            )
        } else {
            state.value.rumblePlayer?.stopPlayer()
            state.value = state.value.copy(rumblePlayer = null)
            lastDisplayedFeed = null
            emitVmEvent(CombinedSearchEvent.PlayVideo(videoEntity))
        }
    }

    override fun onCancelRestricted() {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted(videoEntity: VideoEntity) {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentWatchEvent)
        emitVmEvent(CombinedSearchEvent.PlayVideo(videoEntity))
    }

    private suspend fun fetchData(selection: SortFilterSelection) {
        val result = searchCombineUseCase(
            query,
            selection.sortSelection,
            selection.filterSelection,
            selection.durationSelection
        )
        if (result.channelList.isEmpty() && result.videoList.isEmpty()) {
            state.value = CombineSearchResultState(searchState = SearchState.EMPTY)
        } else if (result.channelList.isEmpty()) {
            state.value = CombineSearchResultState(
                searchState = SearchState.EMPTY_CHANNELS,
                videoList = result.videoList
            )
        } else if (result.videoList.isEmpty()) {
            state.value = CombineSearchResultState(
                searchState = SearchState.EMPTY_VIDEOS,
                channelList = result.channelList
            )
        } else {
            state.value = CombineSearchResultState(
                searchState = SearchState.LOADED,
                channelList = result.channelList,
                videoList = result.videoList
            )
        }
    }

    private fun updateState(updated: VideoEntity) {
        state.value = state.value.copy(
            timeStamp = System.currentTimeMillis(),
            videoList = state.value.videoList.also { videoList ->
                videoList.find { videoEntity -> videoEntity.id == updated.id }?.apply {
                    userVote = updated.userVote
                    likeNumber = updated.likeNumber
                    dislikeNumber = updated.dislikeNumber
                }
            }
        )
    }

    private fun observeSoundState() {
        viewModelScope.launch {
            userPreferenceManager.videoCardSoundStateFlow.collectLatest { enabled ->
                if (enabled.not()) state.value.rumblePlayer?.mute()
                else state.value.rumblePlayer?.unMute()
            }
        }
    }

    private fun emitVmEvent(event: CombinedSearchEvent) =
        viewModelScope.launch { eventFlow.emit(event) }
}