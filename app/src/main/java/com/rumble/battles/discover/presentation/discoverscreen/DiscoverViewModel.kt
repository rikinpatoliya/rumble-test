package com.rumble.battles.discover.presentation.discoverscreen

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.analytics.CardSize
import com.rumble.analytics.DiscoverCategoryClickEvent
import com.rumble.analytics.DiscoverCategoryViewAllEvent
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.domainmodel.discoverScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.common.domain.domainmodel.VideoResult
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryListResult
import com.rumble.domain.discover.domain.usecase.GetEditorPicksVideoListUseCase
import com.rumble.domain.discover.domain.usecase.GetHurryDoNotMissItVideoUseCase
import com.rumble.domain.discover.domain.usecase.GetLiveCategoryListUseCase
import com.rumble.domain.discover.domain.usecase.GetLiveVideoListUseCase
import com.rumble.domain.discover.domain.usecase.GetPopularVideosUseCase
import com.rumble.domain.discover.domain.usecase.GetTopChannelsUseCase
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.utils.RumbleConstants.LIVE_CATEGORIES_LIMIT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import javax.inject.Inject

interface DiscoverHandler: LazyListStateHandler {
    val state: StateFlow<DiscoverState>
    val alertDialogState: State<AlertDialogState>
    val eventFlow: Flow<DiscoverEvent>

    fun refresh()
    fun refreshLiveVideos()
    fun refreshEditorPickVideos()
    fun refreshFeaturedChannels()
    fun refreshHurryDoNotMissItVideo()
    fun refreshPopularVideos()
    fun refreshCategoryList()
    fun like(videoEntity: VideoEntity)
    fun dislike(videoEntity: VideoEntity)
    fun onVideoPlayerImpression()
    fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize)
    fun onSoundClick()
    fun onPauseCurrentPlayer()
    fun onDontMissViewVisible()
    fun onDontMissViewInvisible()
    fun onViewResumed()
    fun onCategoryClick(categoryEntity: CategoryEntity)
    fun onViewAllCategoriesClick()
    fun onVideoItemClick(videoEntity: VideoEntity)
    fun onCancelRestricted()
    fun onWatchRestricted()
}

sealed class DiscoverEvent {
    data class PlayVideo(val videoEntity: VideoEntity) : DiscoverEvent()
}

sealed class DiscoverAlertReason : AlertDialogReason {
    data class RestrictedContentReason(val videoEntity: VideoEntity) : DiscoverAlertReason()
}

private const val TAG = "DiscoverViewModel"

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getLiveVideoListUseCase: GetLiveVideoListUseCase,
    private val getEditorPicksVideoListUseCase: GetEditorPicksVideoListUseCase,
    private val getTopChannelsUseCase: GetTopChannelsUseCase,
    private val getHurryDoNotMissItVideoUseCase: GetHurryDoNotMissItVideoUseCase,
    private val getPopularVideosUseCase: GetPopularVideosUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val getLastPositionUseCase: GetLastPositionUseCase,
    private val getLiveCategoryListUseCase: GetLiveCategoryListUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
) : ViewModel(), DiscoverHandler {
    override val state: MutableStateFlow<DiscoverState> = MutableStateFlow(DiscoverState())
    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())
    override val eventFlow: MutableSharedFlow<DiscoverEvent> = MutableSharedFlow()
    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    private var liveVideoJob: Job? = null
    private var editorPicksJob: Job? = null
    private var featuredChannelsJob: Job? = null
    private var hurryDontMissItJob: Job? = null
    private var popularVideosJob: Job? = null
    private var categoryListJob: Job? = null
    private var dontMissViewVisible = false

    private val errorHandler = CoroutineExceptionHandler { context, throwable ->
        unhandledErrorUseCase(TAG, throwable)

        when (context.job) {
            liveVideoJob -> {
                state.value = state.value.copy(
                    liveNowVideos = emptyList(), liveNowError = true, liveNowLoading = false
                )
            }

            editorPicksJob -> {
                state.value = state.value.copy(
                    editorPicks = emptyList(), editorPicksError = true, editorPicksLoading = false
                )
            }

            featuredChannelsJob -> {
                state.value = state.value.copy(
                    featuredChannels = emptyList(),
                    featuredChannelsError = true,
                    featuredChannelsLoading = false
                )
            }

            hurryDontMissItJob -> {
                state.value = state.value.copy(
                    doNotMissItVideo = null, doNotMissItError = true, doNotMissItLoading = false
                )
            }

            popularVideosJob -> {
                state.value = state.value.copy(
                    popularVideos = emptyList(),
                    popularVideosError = true,
                    popularVideosLoading = false
                )
            }

            categoryListJob -> {
                state.value = state.value.copy(
                    categoryList = emptyList(),
                    categoryListError = true,
                    categoryListLoading = false
                )
            }
        }
    }

    init {
        refresh()
        observeSoundState()
        observePlaybackInFeedMode()
    }

    override fun refresh() {
        refreshLiveVideos()
        refreshEditorPickVideos()
        refreshFeaturedChannels()
        refreshHurryDoNotMissItVideo()
        refreshPopularVideos()
        refreshCategoryList()
    }

    override fun like(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.LIKE)
            if (result.success) updateLikeDislike(result.updatedFeed)
        }
    }

    override fun dislike(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.DISLIKE)
            if (result.success) updateLikeDislike(result.updatedFeed)
        }
    }

    override fun onVideoPlayerImpression() {
        viewModelScope.launch(errorHandler) {
            logVideoPlayerImpressionUseCase(screenId = discoverScreen)
        }
    }

    override fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize) {
        viewModelScope.launch(errorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = videoEntity.videoLogView.view,
                screenId = discoverScreen,
                index = videoEntity.index,
                cardSize = cardSize
            )
        }
    }

    override fun onDontMissViewVisible() {
        viewModelScope.launch {
            state.value.doNotMissItVideo?.let {
                dontMissViewVisible = true
                createDontMissPlayer(it)
                logVideoPlayerImpressionUseCase(screenId = discoverScreen)
            }
        }
    }

    override fun onSoundClick() {
        viewModelScope.launch {
            userPreferenceManager.saveVideoCardSoundState(state.value.soundOn.not())
        }
    }

    private suspend fun createDontMissPlayer(videoEntity: VideoEntity) {
        if (state.value.dontMissPlayer == null) {
            state.update {
                it.copy(
                    dontMissPlayer = initVideoCardPlayerUseCase(
                        videoEntity = videoEntity,
                        screenId = discoverScreen,
                        liveVideoReport = { _, result ->
                            if (result.hasLiveGate) {
                                viewModelScope.launch(Dispatchers.Main) {
                                    state.value.dontMissPlayer?.stopPlayer()
                                    state.value = state.value.copy(dontMissPlayer = null)
                                }
                            }
                        }
                    )
                )
            }
        } else {
            state.value.dontMissPlayer?.playVideo()
            state.value.dontMissPlayer?.seekTo(getLastPositionUseCase(videoEntity.id))
        }
    }

    private fun updateLikeDislike(updated: VideoEntity) {
        state.value.doNotMissItVideo?.let {
            if (it.id == updated.id) {
                state.value = state.value.copy(doNotMissItVideo = updated)
            }
        }

        val updatedPopularVideos = mutableListOf<VideoEntity>()
        state.value.popularVideos.forEach { videoEntity ->
            if (updated.id == videoEntity.id) {
                updatedPopularVideos.add(updated)
            } else {
                updatedPopularVideos.add(videoEntity)
            }
        }

        state.value = state.value.copy(popularVideos = updatedPopularVideos)
    }

    override fun refreshLiveVideos() {
        liveVideoJob?.cancel()
        liveVideoJob = viewModelScope.launch(errorHandler) {
            state.update {
                it.copy(liveNowError = false, liveNowLoading = true)
            }
            when (val result = getLiveVideoListUseCase()) {
                is VideoListResult.Failure ->
                    state.update {
                        it.copy(
                            liveNowVideos = emptyList(),
                            liveNowError = true,
                            liveNowLoading = false
                        )
                    }

                is VideoListResult.Success -> {
                    state.update {
                        it.copy(
                            liveNowVideos = result.videoList,
                            liveNowError = false,
                            liveNowLoading = false
                        )
                    }
                }
            }
        }
    }

    override fun refreshEditorPickVideos() {
        editorPicksJob?.cancel()
        editorPicksJob = viewModelScope.launch(errorHandler) {
            state.update {
                it.copy(editorPicksError = false, editorPicksLoading = true)
            }
            when (val result = getEditorPicksVideoListUseCase()) {
                is VideoListResult.Failure ->
                    state.update {
                        it.copy(
                            editorPicks = emptyList(),
                            editorPicksError = true,
                            editorPicksLoading = false
                        )
                    }

                is VideoListResult.Success -> {
                    state.update {
                        it.copy(
                            editorPicks = result.videoList,
                            editorPicksError = false,
                            editorPicksLoading = false
                        )
                    }
                }
            }
        }
    }

    override fun refreshFeaturedChannels() {
        featuredChannelsJob?.cancel()
        featuredChannelsJob = viewModelScope.launch(errorHandler) {
            state.update {
                it.copy(featuredChannelsError = false, featuredChannelsLoading = true)
            }
            when (val result = getTopChannelsUseCase()) {
                is ChannelListResult.Failure ->
                    state.update {
                        it.copy(
                            featuredChannels = emptyList(),
                            featuredChannelsError = true,
                            featuredChannelsLoading = false
                        )
                    }

                is ChannelListResult.Success -> {
                    state.update {
                        it.copy(
                            featuredChannels = result.channelList,
                            featuredChannelsError = false,
                            featuredChannelsLoading = false
                        )
                    }
                }
            }
        }
    }

    override fun refreshHurryDoNotMissItVideo() {
        hurryDontMissItJob?.cancel()
        hurryDontMissItJob = viewModelScope.launch(errorHandler) {
            state.update {
                it.copy(doNotMissItError = false, doNotMissItLoading = true)
            }
            when (val result = getHurryDoNotMissItVideoUseCase()) {
                is VideoResult.Failure ->
                    state.update {
                        it.copy(
                            doNotMissItVideo = null,
                            doNotMissItError = true,
                            doNotMissItLoading = false
                        )
                    }

                is VideoResult.Success -> {
                    state.update {
                        it.copy(
                            doNotMissItVideo = result.video,
                            doNotMissItError = false,
                            doNotMissItLoading = false
                        )
                    }
                }
            }
        }
    }

    override fun refreshPopularVideos() {
        popularVideosJob?.cancel()
        popularVideosJob = viewModelScope.launch(errorHandler) {
            state.update {
                it.copy(popularVideosError = false, popularVideosLoading = true)
            }
            when (val result = getPopularVideosUseCase()) {
                is VideoListResult.Failure ->
                    state.update {
                        it.copy(
                            popularVideos = emptyList(),
                            popularVideosError = true,
                            popularVideosLoading = false
                        )
                    }

                is VideoListResult.Success -> {
                    state.update {
                        it.copy(
                            popularVideos = result.videoList,
                            popularVideosError = false,
                            popularVideosLoading = false
                        )
                    }
                }
            }
        }
    }

    override fun refreshCategoryList() {
        categoryListJob?.cancel()
        state.update { it.copy(categoryListLoading = true, categoryListError = false) }
        categoryListJob = viewModelScope.launch(errorHandler) {
            when (val result = getLiveCategoryListUseCase(LIVE_CATEGORIES_LIMIT)) {
                is CategoryListResult.Success -> {
                    state.update {
                        it.copy(
                            categoryList = result.categoryList,
                            categoryListLoading = false,
                            categoryListError = false
                        )
                    }
                }

                is CategoryListResult.Failure -> {
                    state.update {
                        it.copy(
                            categoryList = emptyList(),
                            categoryListLoading = false,
                            categoryListError = true
                        )
                    }
                }
            }
        }
    }

    override fun onPauseCurrentPlayer() {
        state.value.dontMissPlayer?.let {
            it.pauseVideo()
            saveLastPositionUseCase(it.currentPositionValue, it.videoId)
        }
    }

    override fun onDontMissViewInvisible() {
        dontMissViewVisible = false
        state.value.dontMissPlayer?.let {
            it.pauseVideo()
            saveLastPositionUseCase(it.currentPositionValue, it.videoId)
        }
        state.value = state.value.copy(
            dontMissPlayer = null
        )
    }

    override fun onViewResumed() {
        if (dontMissViewVisible && state.value.doNotMissItVideo?.ageRestricted?.not() == true)
            state.value.dontMissPlayer?.playVideo()
    }

    override fun onCategoryClick(categoryEntity: CategoryEntity) =
        analyticsEventUseCase(DiscoverCategoryClickEvent(categoryEntity.title))

    override fun onViewAllCategoriesClick() =
        analyticsEventUseCase(DiscoverCategoryViewAllEvent)

    override fun onVideoItemClick(videoEntity: VideoEntity) {
        if (videoEntity.ageRestricted) {
            alertDialogState.value = AlertDialogState(
                true,
                DiscoverAlertReason.RestrictedContentReason(videoEntity)
            )
        } else {
            emitVmEvent(DiscoverEvent.PlayVideo(videoEntity))
        }
    }

    override fun onCancelRestricted() {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted() {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentWatchEvent)
        dontMissViewVisible = false
        state.value.dontMissPlayer?.let {
            it.pauseVideo()
            saveLastPositionUseCase(it.currentPositionValue, it.videoId)
        }
        state.value.doNotMissItVideo?.let {
            emitVmEvent(DiscoverEvent.PlayVideo(it))
        }
    }

    private fun observeSoundState() {
        viewModelScope.launch {
            userPreferenceManager.videoCardSoundStateFlow.collectLatest { enabled ->
                if (enabled.not()) state.value.dontMissPlayer?.mute()
                else state.value.dontMissPlayer?.unMute()
                state.update {
                    it.copy(soundOn = enabled)
                }
            }
        }
    }

    private fun observePlaybackInFeedMode() {
        viewModelScope.launch {
            userPreferenceManager.playbackInFeedsModeModeFlow.distinctUntilChanged().collectLatest {
                state.update { state.value.copy(dontMissPlayer = null) }
                onDontMissViewVisible()
            }
        }
    }

    private fun emitVmEvent(event: DiscoverEvent) =
        viewModelScope.launch { eventFlow.emit(event) }
}