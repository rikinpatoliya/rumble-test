package com.rumble.battles.library.presentation.library

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.analytics.CardSize
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.domainmodel.libraryScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.domainmodel.PlayListsResult
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntityWithOptions
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.library.domain.model.LibraryCollection
import com.rumble.domain.library.domain.usecase.GetLibraryPlayListsUseCase
import com.rumble.domain.library.domain.usecase.GetLibraryShortListUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListOptionsUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface LibraryHandler: LazyListStateHandler {
    val state: StateFlow<LibraryScreenUIState>
    val alertDialogState: StateFlow<AlertDialogState>
    val eventFlow: Flow<LibraryScreenVmEvent>

    fun refresh()
    fun refreshWatchHistory()
    fun refreshPurchasesVideos()
    fun refreshWatchLaterVideos()
    fun refreshPlayListsVideos()
    //TODO: @LibraryFeatureWIP Hiding LikedVideos
    // fun refreshLikedVideos()
    fun onPlayListUpdated(playListEntity: PlayListEntity)
    fun onVideoItemClick(videoEntity: VideoEntity)
    fun onVideoCardImpression(videoEntity: VideoEntity)
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity)
}

data class LibraryScreenUIState(
    val watchHistoryLoading: Boolean = false,
    val watchHistoryVideos: List<VideoEntity> = emptyList(),
    val watchHistoryError: Boolean = false,

    val purchasesLoading: Boolean = false,
    val purchasesVideos: List<VideoEntity> = emptyList(),
    val purchasesError: Boolean = false,

    val watchLaterLoading: Boolean = false,
    val watchLaterVideos: List<VideoEntity> = emptyList(),
    val watchLaterError: Boolean = false,

    val playListsLoading: Boolean = false,
    val playListEntities: List<PlayListEntityWithOptions> = emptyList(),
    val playListsError: Boolean = false,

//    val likedVideosLoading: Boolean = false,
//    val likedVideos: List<VideoEntity> = emptyList(),
//    val likedVideosError: Boolean = false,
)

sealed class LibraryScreenVmEvent {
    data class PlayVideo(val videoEntity: VideoEntity) : LibraryScreenVmEvent()
}

sealed class LibraryAlertReason : AlertDialogReason {
    data class RestrictedContentReason(val videoEntity: VideoEntity) : LibraryAlertReason()
}

private const val TAG = "LibraryViewModel"

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val getLibraryShortListUseCase: GetLibraryShortListUseCase,
    private val getLibraryPlayListsUseCase: GetLibraryPlayListsUseCase,
    private val getPlayListOptionsUseCase: GetPlayListOptionsUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    userPreferenceManager: UserPreferenceManager,
) : ViewModel(), LibraryHandler {
    override val state = MutableStateFlow(LibraryScreenUIState())
    override val alertDialogState = MutableStateFlow(AlertDialogState())
    override val eventFlow: MutableSharedFlow<LibraryScreenVmEvent> = MutableSharedFlow()
    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    private var watchHistoryJob: Job? = null
    private var purchasesJob: Job? = null
    private var watchLaterJob: Job? = null
    private var playListsJob: Job? = null
    private var likedVideosJob: Job? = null

    private val errorHandler = CoroutineExceptionHandler { context, throwable ->
        handleFailure(context, throwable)
    }

    init {
        observeUserAuthState()
        refresh()
    }

    override fun refresh() {
        refreshWatchHistory()
        refreshPurchasesVideos()
        refreshWatchLaterVideos()
        refreshPlayListsVideos()
//        refreshLikedVideos()
    }

    override fun refreshWatchHistory() {
        watchHistoryJob?.cancel()
        watchHistoryJob = viewModelScope.launch(errorHandler) {
            state.update {
                it.copy(watchHistoryError = false, watchHistoryLoading = true)
            }
            when (val result = getLibraryShortListUseCase(LibraryCollection.WatchHistory)) {
                is VideoListResult.Failure -> {
                    state.update {
                        it.copy(
                            watchHistoryVideos = emptyList(),
                            watchHistoryError = true,
                            watchHistoryLoading = false
                        )
                    }
                }

                is VideoListResult.Success -> {
                    state.update {
                        it.copy(
                            watchHistoryVideos = result.videoList,
                            watchHistoryError = false,
                            watchHistoryLoading = false
                        )
                    }
                }
            }
        }
    }

    override fun refreshPurchasesVideos() {
        purchasesJob?.cancel()
        purchasesJob = viewModelScope.launch(errorHandler) {
            state.update {
                it.copy(purchasesError = false, purchasesLoading = true)
            }
            when (val result = getLibraryShortListUseCase(LibraryCollection.Purchases)) {
                is VideoListResult.Failure -> {
                    state.update {
                        it.copy(
                            purchasesVideos = emptyList(),
                            purchasesError = true,
                            purchasesLoading = false
                        )
                    }
                }

                is VideoListResult.Success -> {
                    state.update {
                        it.copy(
                            purchasesVideos = result.videoList,
                            purchasesError = false,
                            purchasesLoading = false
                        )
                    }
                }
            }
        }
    }

    override fun refreshWatchLaterVideos() {
        watchLaterJob?.cancel()
        watchLaterJob = viewModelScope.launch(errorHandler) {
            state.update {
                it.copy(watchLaterError = false, watchLaterLoading = true)
            }
            when (val result = getLibraryShortListUseCase(LibraryCollection.WatchLater)) {
                is VideoListResult.Failure -> {
                    state.update {
                        it.copy(
                            watchLaterVideos = emptyList(),
                            watchLaterError = true,
                            watchLaterLoading = false
                        )
                    }
                }

                is VideoListResult.Success -> {
                    state.update {
                        it.copy(
                            watchLaterVideos = result.videoList,
                            watchLaterError = false,
                            watchLaterLoading = false
                        )
                    }
                }
            }
        }
    }

    override fun refreshPlayListsVideos() {
        playListsJob?.cancel()
        playListsJob = viewModelScope.launch(errorHandler) {
            state.update {
                it.copy(playListsError = false, playListsLoading = true)
            }
            when (val result = getLibraryPlayListsUseCase()) {
                is PlayListsResult.Failure -> {
                    state.update {
                        it.copy(
                            playListEntities = emptyList(),
                            playListsError = true,
                            playListsLoading = false
                        )
                    }
                }

                is PlayListsResult.Success -> {
                    state.update {
                        it.copy(
                            playListEntities = result.playListEntities.map { entity ->
                                PlayListEntityWithOptions(
                                    playListEntity = entity,
                                    playListOptions = getPlayListOptionsUseCase(
                                        playListEntity = entity,
                                        userId = sessionManager.userIdFlow.first(),
                                    )
                                )
                            },
                            playListsError = false,
                            playListsLoading = false
                        )
                    }
                }
            }
        }
    }

//    override fun refreshLikedVideos() {
//        likedVideosJob?.cancel()
//        likedVideosJob = viewModelScope.launch(errorHandler) {
//            state.update {
//                it.copy(likedVideosError = false, likedVideosLoading = true)
//            }
//            when (val result = getLibraryShortListUseCase(LibraryCollection.Liked)) {
//                is VideoListResult.Failure -> {
//                    state.update {
//                        it.copy(
//                            likedVideos = emptyList(),
//                            likedVideosError = true,
//                            likedVideosLoading = false
//                        )
//                    }
//                }
//
//                is VideoListResult.Success -> {
//                    state.update {
//                        it.copy(
//                            likedVideos = result.videoList,
//                            likedVideosError = false,
//                            likedVideosLoading = false
//                        )
//                    }
//                }
//            }
//        }
//    }

    override fun onPlayListUpdated(playListEntity: PlayListEntity) {
        state.update { uiState ->
            uiState.copy(
                playListEntities = uiState.playListEntities.map {
                    if (it.playListEntity.id == playListEntity.id)
                        it.copy(
                            playListEntity = it.playListEntity.copy(
                                title = playListEntity.title,
                                description = playListEntity.description,
                                playListOwnerId = playListEntity.playListOwnerId,
                                channelName = playListEntity.channelName,
                                username = playListEntity.username,
                                visibility = playListEntity.visibility
                            )
                        )
                    else
                        it
                }
            )
        }
    }

    override fun onVideoItemClick(videoEntity: VideoEntity) {
        if (videoEntity.ageRestricted) {
            alertDialogState.value = AlertDialogState(
                true,
                LibraryAlertReason.RestrictedContentReason(videoEntity)
            )
        } else {
            emitVmEvent(LibraryScreenVmEvent.PlayVideo(videoEntity))
        }
    }

    override fun onVideoCardImpression(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = videoEntity.videoLogView.view,
                screenId = libraryScreen,
                index = videoEntity.index,
                cardSize = CardSize.COMPACT
            )
        }
    }

    override fun onCancelRestricted() {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted(videoEntity: VideoEntity) {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentWatchEvent)
        emitVmEvent(LibraryScreenVmEvent.PlayVideo(videoEntity))
    }

    private fun observeUserAuthState() {
        viewModelScope.launch {
            sessionManager.cookiesFlow.distinctUntilChanged().collectLatest { cookies ->
                if (cookies.isNotBlank()) {
                    refresh()
                }
            }
        }
    }

    private fun handleFailure(context: CoroutineContext, throwable: Throwable) {
        unhandledErrorUseCase(TAG, throwable)
        when (context.job) {
            watchHistoryJob -> {
                state.value = state.value.copy(
                    watchHistoryVideos = emptyList(),
                    watchHistoryError = true,
                    watchHistoryLoading = false
                )
            }

            purchasesJob -> {
                state.value = state.value.copy(
                    purchasesVideos = emptyList(), purchasesError = true, purchasesLoading = false
                )
            }

            watchLaterJob -> {
                state.value = state.value.copy(
                    watchLaterVideos = emptyList(),
                    watchLaterError = true,
                    watchLaterLoading = false
                )
            }

            playListsJob -> {
                state.value = state.value.copy(
                    playListEntities = emptyList(), playListsError = true, playListsLoading = false
                )
            }

//            likedVideosJob -> {
//                state.value = state.value.copy(
//                    likedVideos = emptyList(), likedVideosError = true, likedVideosLoading = false
//                )
//            }
        }
    }

    private fun emitVmEvent(event: LibraryScreenVmEvent) =
        viewModelScope.launch { eventFlow.emit(event) }
}