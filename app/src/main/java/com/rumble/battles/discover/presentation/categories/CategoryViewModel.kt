package com.rumble.battles.discover.presentation.categories

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.analytics.BrowseCategoriesBackButtonEvent
import com.rumble.analytics.BrowseCategoriesCategoryButtonEvent
import com.rumble.analytics.BrowseCategoriesCategoryCardEvent
import com.rumble.analytics.BrowseCategoriesLiveStreamTabTapEvent
import com.rumble.analytics.BrowseCategoriesSearchEvent
import com.rumble.analytics.BrowseCategoriesTabTapEvent
import com.rumble.analytics.BrowseCategoriesVideoCardEvent
import com.rumble.analytics.CardSize
import com.rumble.analytics.CategoryBackButtonEvent
import com.rumble.analytics.CategoryCardEvent
import com.rumble.analytics.CategoryCategoriesTabTapEvent
import com.rumble.analytics.CategoryLiveStreamTabTapEvent
import com.rumble.analytics.CategoryRecordedTabTapEvent
import com.rumble.analytics.CategorySearchEvent
import com.rumble.analytics.CategoryVideoCardEvent
import com.rumble.analytics.CategoryVideosTabTapEvent
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.common.presentation.LazyGridStateHandler
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.RumblePath
import com.rumble.domain.analytics.domain.domainmodel.categoryScreen
import com.rumble.domain.analytics.domain.domainmodel.videoDetailsScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.discover.domain.domainmodel.CategoryDisplayType
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryListResult
import com.rumble.domain.discover.domain.domainmodel.CategoryResult
import com.rumble.domain.discover.domain.usecase.GetCategoryLiveVideoListUseCase
import com.rumble.domain.discover.domain.usecase.GetCategoryUseCase
import com.rumble.domain.discover.domain.usecase.GetCategoryVideoListUseCase
import com.rumble.domain.discover.domain.usecase.GetLiveCategoryListUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.videoplayer.player.RumblePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface CategoryHandler : LazyListStateHandler, LazyGridStateHandler {
    val state: StateFlow<CategoryState>
    val currentPlayerState: State<RumblePlayer?>
    val soundState: Flow<Boolean>
    val alertDialogState: State<AlertDialogState>
    val eventFlow: Flow<CategoryEvent>

    fun fetchCategoryData()
    fun onDisplayTypeSelected(displayType: CategoryDisplayType)
    fun onLike(videoEntity: VideoEntity)
    fun onDislike(videoEntity: VideoEntity)
    fun onFullyVisibleFeedChanged(feed: Feed?)
    fun onCreatePlayerForVisibleFeed()
    fun onSoundClick()
    fun onPauseCurrentPlayer()
    fun onViewResumed()
    fun onVideoCardImpression(videoEntity: VideoEntity)
    fun onPlayerImpression(videoEntity: VideoEntity)
    fun onBackClick()
    fun onCategoryButtonClick(categoryName: String)
    fun onCategoryCardClick(categoryEntity: CategoryEntity, index: Int)
    fun onSearch()
    fun onVideoClick(feed: Feed, index: Int)
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity, index: Int)
    fun onRefresh()
}

data class CategoryState(
    val categoryPath: String = "",
    val category: CategoryEntity? = null,
    val displayType: CategoryDisplayType,
    val subcategoryList: List<CategoryEntity> = emptyList(),
    val categoryList: List<CategoryEntity> = emptyList(),
    val videoList: Flow<PagingData<Feed>> = emptyFlow(),
    val liveVideoList: Flow<PagingData<Feed>> = emptyFlow(),
    val showLiveCategoryList: Boolean = true,
    val displayErrorView: Boolean = false,
    val updatedVideo: VideoEntity? = null,
    val isLoading: Boolean = true
)

sealed class CategoryEvent {
    data class PlayVideo(val videoEntity: VideoEntity) : CategoryEvent()
}

sealed class CategoryAlertReason : AlertDialogReason {
    data class RestrictedContentReason(val videoEntity: VideoEntity, val index: Int) :
        CategoryAlertReason()
}

private const val TAG = "CategoryViewModel"

@HiltViewModel
class CategoryViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val getCategoryVideoListUseCase: GetCategoryVideoListUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val getLastPositionUseCase: GetLastPositionUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase,
    private val getLiveCategoryListUseCase: GetLiveCategoryListUseCase,
    private val getCategoryLiveVideoListUseCase: GetCategoryLiveVideoListUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
) : ViewModel(), CategoryHandler {

    private var currentVisibleFeed: VideoEntity? = null
    private var lastDisplayedFeed: VideoEntity? = null
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        state.update {
            state.value.copy(
                displayErrorView = true,
                isLoading = false
            )
        }
    }

    override val state: MutableStateFlow<CategoryState> =
        MutableStateFlow(
            CategoryState(
                categoryPath = savedState[RumblePath.VIDEO_CATEGORY.path] ?: "",
                showLiveCategoryList = savedState[RumblePath.PARAMETER.path] ?: true,
                displayType = CategoryDisplayType.getByName(
                    savedState[RumblePath.TYPE.path] ?: ""
                ) ?: CategoryDisplayType.LIVE_STREAM
            )
        )
    override val currentPlayerState: MutableState<RumblePlayer?> = mutableStateOf(null)
    override val soundState = userPreferenceManager.videoCardSoundStateFlow
    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())
    override val eventFlow: MutableSharedFlow<CategoryEvent> = MutableSharedFlow()

    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    override var gridState: MutableState<LazyGridState> = mutableStateOf(LazyGridState(0, 0))

    init {
        observeSoundState()
        onRefresh()
    }

    override fun onDisplayTypeSelected(displayType: CategoryDisplayType) {
        logTabEvent(displayType)
        if (displayType != CategoryDisplayType.CATEGORIES && state.value.category != null) {
            state.value.category?.let { category ->
                state.update {
                    state.value.copy(
                        videoList = getCategoryVideoListUseCase(
                            category = category,
                            displayType = displayType,
                            showLiveCategoryList = state.value.showLiveCategoryList,
                            subcategoryList = state.value.subcategoryList
                        ).cachedIn(viewModelScope),
                        displayType = displayType
                    )
                }
            }
        } else {
            state.update {
                state.value.copy(
                    displayType = displayType
                )
            }
        }
    }

    override fun fetchCategoryData() {
        viewModelScope.launch(errorHandler) {
            val result = getCategoryUseCase(state.value.categoryPath)
            if (result is CategoryResult.Success) {
                state.update {
                    state.value.copy(
                        category = result.category,
                        videoList = getCategoryVideoListUseCase(
                            category = result.category,
                            displayType = state.value.displayType,
                            showLiveCategoryList = state.value.showLiveCategoryList,
                            subcategoryList = result.subcategoryList
                        ).cachedIn(viewModelScope),
                        subcategoryList = result.subcategoryList,
                        displayErrorView = false,
                        isLoading = false
                    )
                }
            } else {
                state.update {
                    state.value.copy(
                        displayErrorView = true,
                        isLoading = false
                    )
                }
            }
        }
    }

    override fun onLike(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.LIKE)
            if (result.success) state.update {
                state.value.copy(updatedVideo = result.updatedFeed)
            }
        }
    }

    override fun onDislike(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.DISLIKE)
            if (result.success) state.update {
                state.value.copy(updatedVideo = result.updatedFeed)
            }
        }
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
                    currentPlayerState.value = initVideoCardPlayerUseCase(
                        videoEntity = it,
                        screenId = categoryScreen,
                        liveVideoReport = { _, result ->
                            if (result.hasLiveGate) {
                                viewModelScope.launch(Dispatchers.Main) {
                                    currentPlayerState.value?.stopPlayer()
                                    currentPlayerState.value = null
                                    lastDisplayedFeed = null
                                }
                            }
                        }
                    )
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
        currentPlayerState.value?.pauseVideo()
    }

    override fun onViewResumed() {
        viewModelScope.launch {
            currentVisibleFeed?.id?.let {
                currentPlayerState.value?.let { player ->
                    if (player.currentVideoAgeRestricted.not()) {
                        player.seekTo(getLastPositionUseCase(it))
                        player.playVideo()
                    }
                }
            }
        }
    }

    override fun onVideoCardImpression(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = videoEntity.videoLogView.view,
                screenId = categoryScreen,
                index = videoEntity.index,
                cardSize = CardSize.REGULAR
            )
        }
    }

    override fun onPlayerImpression(videoEntity: VideoEntity) {
        viewModelScope.launch {
            logVideoPlayerImpressionUseCase(
                screenId = categoryScreen,
                index = videoEntity.index,
                cardSize = CardSize.REGULAR,
            )
        }
    }

    override fun onBackClick() {
        if (state.value.categoryPath.isEmpty()) {
            analyticsEventUseCase(BrowseCategoriesBackButtonEvent)
        } else {
            analyticsEventUseCase(CategoryBackButtonEvent)
        }
    }

    override fun onCategoryButtonClick(categoryName: String) {
        analyticsEventUseCase(BrowseCategoriesCategoryButtonEvent(categoryName))
    }

    override fun onCategoryCardClick(categoryEntity: CategoryEntity, index: Int) {
        if (state.value.categoryPath.isEmpty()) {
            analyticsEventUseCase(BrowseCategoriesCategoryCardEvent(categoryEntity.title, index))
        } else {
            analyticsEventUseCase(
                CategoryCardEvent(
                    categoryEntity.title,
                    index,
                    state.value.displayType.analyticName
                )
            )
        }
    }

    override fun onSearch() {
        if (state.value.categoryPath.isEmpty()) {
            analyticsEventUseCase(BrowseCategoriesSearchEvent)
        } else {
            analyticsEventUseCase(CategorySearchEvent)
        }
    }

    override fun onVideoClick(feed: Feed, index: Int) {
        (feed as? VideoEntity)?.let { videoEntity ->
            if (videoEntity.ageRestricted) {
                alertDialogState.value = AlertDialogState(
                    true,
                    CategoryAlertReason.RestrictedContentReason(videoEntity, index)
                )
            } else {
                if (state.value.categoryPath.isEmpty()) {
                    analyticsEventUseCase(BrowseCategoriesVideoCardEvent(videoDetailsScreen, index))
                } else {
                    analyticsEventUseCase(
                        CategoryVideoCardEvent(
                            videoDetailsScreen,
                            index,
                            state.value.displayType.analyticName
                        )
                    )
                }
                onWatchVideo(videoEntity)
            }
        }
    }

    override fun onCancelRestricted() {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted(videoEntity: VideoEntity, index: Int) {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentWatchEvent)
        onWatchVideo(videoEntity)
    }

    override fun onRefresh() {
        if (state.value.categoryPath.isEmpty()) {
            fetchLiveCategoryList()
            fetchCategoryLiveVideoList()
        } else {
            fetchCategoryData()
        }
    }

    private fun onWatchVideo(videoEntity: VideoEntity) {
        currentPlayerState.value?.currentPositionValue?.let { position ->
            currentVisibleFeed?.id?.let { videoId ->
                if (videoEntity.id == videoId) saveLastPosition(position, videoId)
            }
        }
        currentPlayerState.value?.stopPlayer()
        currentPlayerState.value = null
        lastDisplayedFeed = null
        emitVmEvent(CategoryEvent.PlayVideo(videoEntity))
    }

    private fun fetchLiveCategoryList() {
        viewModelScope.launch(errorHandler) {
            val result = getLiveCategoryListUseCase()
            if (result is CategoryListResult.Success && result.categoryList.isNotEmpty()) {
                state.update {
                    state.value.copy(
                        categoryList = result.categoryList,
                        isLoading = false
                    )
                }
            } else {
                state.update {
                    state.value.copy(
                        categoryList = emptyList(),
                        isLoading = false,
                        displayErrorView = true
                    )
                }
            }
        }
    }

    private fun fetchCategoryLiveVideoList() {
        state.update {
            state.value.copy(
                liveVideoList = getCategoryLiveVideoListUseCase().cachedIn(viewModelScope),
                isLoading = false,
                displayErrorView = false
            )
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

    private fun logTabEvent(displayType: CategoryDisplayType) {
        when (displayType) {
            CategoryDisplayType.CATEGORIES -> {
                if (state.value.categoryPath.isEmpty()) {
                    analyticsEventUseCase(BrowseCategoriesTabTapEvent)
                } else {
                    analyticsEventUseCase(CategoryCategoriesTabTapEvent)
                }
            }

            CategoryDisplayType.LIVE_STREAM -> {
                if (state.value.categoryPath.isEmpty()) {
                    analyticsEventUseCase(BrowseCategoriesLiveStreamTabTapEvent)
                } else {
                    analyticsEventUseCase(CategoryLiveStreamTabTapEvent)
                }
            }

            CategoryDisplayType.RECORDED_STREAM ->
                analyticsEventUseCase(CategoryRecordedTabTapEvent)

            CategoryDisplayType.VIDEOS ->
                analyticsEventUseCase(CategoryVideosTabTapEvent)
            else -> Unit
        }
    }

    private fun saveLastPosition(lastPosition: Long, videoId: Long) {
        viewModelScope.launch {
            if (soundState.first()) saveLastPositionUseCase(lastPosition, videoId)
        }
    }

    private fun emitVmEvent(event: CategoryEvent) =
        viewModelScope.launch { eventFlow.emit(event) }
}