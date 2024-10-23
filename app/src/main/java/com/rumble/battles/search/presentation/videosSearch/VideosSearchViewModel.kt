package com.rumble.battles.search.presentation.videosSearch

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.analytics.CardSize
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.common.presentation.LazyListStateHandler
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.RumblePath
import com.rumble.battles.sort.SortFilterSelection
import com.rumble.domain.analytics.domain.domainmodel.videoSearchScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.search.domain.useCases.SearchVideosUseCase
import com.rumble.domain.sort.DurationType
import com.rumble.domain.sort.FilterType
import com.rumble.domain.sort.SortType
import com.rumble.utils.extension.navigationSafeDecode
import com.rumble.utils.valueOfOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface VideosSearchHandler: LazyListStateHandler {
    val query: String
    val videoList: Flow<PagingData<VideoEntity>>
    val selection: SortFilterSelection
    val alertDialogState: State<AlertDialogState>
    val eventFlow: Flow<VideosSearchEvent>

    fun onSelectionMade(newSelection: SortFilterSelection)
    fun onVideoCardImpression(videoEntity: VideoEntity)
    fun onVideoItemClick(videoEntity: VideoEntity)
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity)
}

sealed class VideosSearchEvent {
    data class PlayVideo(val videoEntity: VideoEntity) : VideosSearchEvent()
}

sealed class VideosSearchAlertReason : AlertDialogReason {
    data class RestrictedContentReason(val videoEntity: VideoEntity) : VideosSearchAlertReason()
}

private const val TAG = "VideosSearchViewModel"

@HiltViewModel
class VideosSearchViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val searchVideosUseCase: SearchVideosUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
) : ViewModel(), VideosSearchHandler {

    override val query: String =
        (stateHandle.get<String>(RumblePath.QUERY.path) ?: "").navigationSafeDecode()

    override var selection: SortFilterSelection = SortFilterSelection(
        sortSelection = valueOfOrNull<SortType>(stateHandle.get<String>(RumblePath.SORT.path))
            ?: SortType.values().first(),
        filterSelection = valueOfOrNull<FilterType>(stateHandle.get<String>(RumblePath.UPLOAD_DATE.path))
            ?: FilterType.values().first(),
        durationSelection = valueOfOrNull<DurationType>(stateHandle.get<String>(RumblePath.DURATION.path))
            ?: DurationType.values().first()
    )

    override val alertDialogState: MutableState<AlertDialogState> =
        mutableStateOf(AlertDialogState())

    override val eventFlow: MutableSharedFlow<VideosSearchEvent> = MutableSharedFlow()

    override var videoList: Flow<PagingData<VideoEntity>> =
        searchVideosUseCase(
            query,
            selection.sortSelection,
            selection.filterSelection,
            selection.durationSelection
        ).cachedIn(viewModelScope)

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    override var listState: MutableState<LazyListState> = mutableStateOf(LazyListState(0, 0))

    override fun updateListState(newState: LazyListState) {
        listState.value = newState
    }


    override fun onSelectionMade(newSelection: SortFilterSelection) {
        selection = newSelection

        videoList = searchVideosUseCase(
            query,
            selection.sortSelection,
            selection.filterSelection,
            selection.durationSelection
        ).cachedIn(viewModelScope)
    }

    override fun onVideoCardImpression(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = videoEntity.videoLogView.view,
                screenId = videoSearchScreen,
                index = videoEntity.index,
                cardSize = CardSize.COMPACT
            )
        }
    }

    override fun onVideoItemClick(videoEntity: VideoEntity) {
        if (videoEntity.ageRestricted) {
            alertDialogState.value = AlertDialogState(
                true,
                VideosSearchAlertReason.RestrictedContentReason(videoEntity)
            )
        } else {
            emitVmEvent(VideosSearchEvent.PlayVideo(videoEntity))
        }
    }

    override fun onCancelRestricted() {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted(videoEntity: VideoEntity) {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentWatchEvent)
        emitVmEvent(VideosSearchEvent.PlayVideo(videoEntity))
    }

    private fun emitVmEvent(event: VideosSearchEvent) =
        viewModelScope.launch { eventFlow.emit(event) }
}