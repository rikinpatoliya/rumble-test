package com.rumble.ui3.channel.details.v4

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelVideosUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.ReportChannelUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelSubscriptionUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import com.rumble.network.queryHelpers.Sort
import com.rumble.util.Constant
import com.rumble.videoplayer.player.config.ReportType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "ChannelDetailsViewModel"

interface ChannelDetailsHandler {
    val uiState: StateFlow<ChannelDetailsUIState>
    val pagingDataState: StateFlow<Flow<PagingData<Feed>>>

    val vmEvents: Flow<ChannelDetailsVmEvent>

    val channelDetails: MutableLiveData<CreatorEntity>

    fun onLoadInitialData(cachedState: ChannelDetailsUIState?, cachedPagingData: Flow<PagingData<Feed>>?)
    fun onError(throwable: Throwable)
    fun onLoadChannelData()
    fun onUpdateSubscription()
    fun onLastFocusRestored()

    fun onFocusChanged(focusedView: FocusViews)
    fun onVideoItemClicked(videoItem: VideoEntity)
    fun onVideoItemFocusChanged(index: Int)
    fun onSortChanged(sort: Sort)
    fun onChannelReported()

    /* TODO These are just being moved over from old implementation but should be refactored */
    var channelObject: CreatorEntity?
    var channelId: String?
    fun getVideoCollectionItemFlowSort(sort: Sort): Flow<PagingData<Feed>>?
    fun onLoadChannelVideos()
}

enum class FocusViews {
    NONE,
    FOLLOW,
    MORE_BUTTON,
    MOST_RECENT,
    MOST_VIEWED,
    GRID
}

data class ChannelDetailsUIState(
    val channelId: String = "",
    val sort: Sort = Sort.DATE,

    val channelVideoLoadTimeStamp: Long = 0,
    val lastSelectedDetailsItemPosition: Int = -1,

    val currentFocusedViews: FocusViews = FocusViews.NONE,
    val lastSelectedView: FocusViews? = FocusViews.GRID,
)

sealed class ChannelDetailsVmEvent {
    object RefreshVideosIfEmpty : ChannelDetailsVmEvent()
    data class NavigateToVideoPlayer(val videoEntity: VideoEntity) : ChannelDetailsVmEvent()
    object ShowVideoPlayerError : ChannelDetailsVmEvent()
    object ShowChannelReported : ChannelDetailsVmEvent()
}

@HiltViewModel
class ChannelDetailsViewModelV4 @Inject constructor(
    private val getChannelVideosUseCase: GetChannelVideosUseCase,
    private val getChannelDataUseCase: GetChannelDataUseCase,
    private val updateChannelSubscriptionUseCase: UpdateChannelSubscriptionUseCase,
    private val reportChannelUseCase: ReportChannelUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
    private val internetConnectionUseCase: InternetConnectionUseCase,
) : ViewModel(), ChannelDetailsHandler {

    override val uiState = MutableStateFlow(ChannelDetailsUIState())
    override val pagingDataState = MutableStateFlow<Flow<PagingData<Feed>>>(emptyFlow())

    override var channelObject: CreatorEntity? = null
    override var channelId: String? = null

    private val _vmEvents = Channel<ChannelDetailsVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<ChannelDetailsVmEvent> = _vmEvents.receiveAsFlow()

    override val channelDetails: MutableLiveData<CreatorEntity> by lazy {
        MutableLiveData<CreatorEntity>()
    }

    private fun isRefreshNeeded(cachedState: ChannelDetailsUIState): Boolean =
        System.currentTimeMillis() - cachedState.channelVideoLoadTimeStamp > Constant.REFRESH_CONTENT_DURATION && cachedState.sort == uiState.value.sort

    override fun onLoadInitialData(cachedState: ChannelDetailsUIState?, cachedPagingData: Flow<PagingData<Feed>>?) {
        if (cachedState != null && isRefreshNeeded(cachedState).not() && cachedPagingData != null) {
            uiState.value = cachedState
            pagingDataState.value = cachedPagingData
        } else {
            onLoadChannelVideos()
        }

        onLoadChannelData()
    }

    override fun onLoadChannelVideos() {
        viewModelScope.launch(errorHandler) {
            val pagingData = getChannelVideosUseCase(channelId!!, uiState.value.sort)
            uiState.value = uiState.value.copy(
                channelId = channelId!!,
                channelVideoLoadTimeStamp = System.currentTimeMillis()
            )

            pagingDataState.value = pagingData
        }
    }

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    val connectionState: MutableLiveData<InternetConnectionState> = MutableLiveData<InternetConnectionState>()

    init {
        observeConnectionState()
    }

    override fun getVideoCollectionItemFlowSort(sort: Sort): Flow<PagingData<Feed>>? =
        channelId?.let { getChannelVideosUseCase(it, sort).cachedIn(viewModelScope) }

    override fun onLoadChannelData() {
        viewModelScope.launch(errorHandler) {
            channelObject = channelId?.let { getChannelDataUseCase(it).getOrNull() }
            channelDetails.value = channelObject
        }
    }

    override fun onUpdateSubscription() {
        viewModelScope.launch(errorHandler) {
            channelObject?.let {
                val channelAction = if (it.blocked) {
                    UpdateChannelSubscriptionAction.UNBLOCK
                } else if (it.followed) {
                    UpdateChannelSubscriptionAction.UNSUBSCRIBE
                } else {
                    UpdateChannelSubscriptionAction.SUBSCRIBE
                }

                updateChannelSubscriptionUseCase(it, channelAction)
                    .onSuccess { channelsDetailsEntity ->
                        channelObject = channelsDetailsEntity
                        channelDetails.value = channelsDetailsEntity
                    }
                    .onFailure {
                        channelObject = null
                    }
            }
            Timber.d("\n\nchannelObject: $channelObject")
        }
    }

    override fun onLastFocusRestored() {
        uiState.value = uiState.value.copy(
            currentFocusedViews = FocusViews.NONE
        )
    }

    override fun onFocusChanged(focusedView: FocusViews) {
        when (focusedView) {
            FocusViews.NONE -> {}
            FocusViews.MOST_RECENT, FocusViews.MOST_VIEWED, FocusViews.MORE_BUTTON, FocusViews.FOLLOW -> {
                uiState.value = uiState.value.copy(
                    currentFocusedViews = focusedView,
                    lastSelectedView = focusedView
                )
            }

            FocusViews.GRID -> {
                uiState.value =
                    uiState.value.copy(
                        currentFocusedViews = focusedView,
                        lastSelectedView = FocusViews.NONE
                    )
            }
        }
    }

    override fun onVideoItemClicked(videoItem: VideoEntity) {
        if (videoItem.videoSourceList.isEmpty().not()) {
            sendEvent(ChannelDetailsVmEvent.NavigateToVideoPlayer(videoItem))
        } else {
            sendEvent(ChannelDetailsVmEvent.ShowVideoPlayerError)
        }
        uiState.value = uiState.value.copy(lastSelectedView = FocusViews.NONE)
    }

    override fun onVideoItemFocusChanged(index: Int) {
        uiState.value = uiState.value.copy(lastSelectedDetailsItemPosition = index)
    }

    override fun onSortChanged(sort: Sort) {
        viewModelScope.launch(errorHandler) {
            uiState.value = uiState.value.copy(sort = sort)
            onLoadChannelVideos()
        }
    }

    override fun onChannelReported() {
        _vmEvents.trySend(ChannelDetailsVmEvent.ShowChannelReported)
    }

    fun blockAndUnblock(subscriptionAction: UpdateChannelSubscriptionAction): CreatorEntity? {
        return runBlocking(errorHandler) {
            channelObject?.let {
                updateChannelSubscriptionUseCase(
                    it,
                    subscriptionAction
                ).getOrNull()
            }
        }
    }

    fun reportAsSpam() {
        onReport(ReportType.SPAM)
    }

    fun reportAsInappropriate() {
        onReport(ReportType.INAPPROPRIATE)
    }

    fun reportAsViolatingCopyright() {
        onReport(ReportType.COPYRIGHT)
    }

    fun reportAsViolatingTerms() {
        onReport(ReportType.TERMS)
    }

    private fun onReport(reason: ReportType) {
        viewModelScope.launch(errorHandler) {
            channelObject?.let {
                reportChannelUseCase(it, reason)
            }
        }
    }

    override fun onError(throwable: Throwable) {
        unhandledErrorUseCase(TAG, throwable)
    }

    private fun observeConnectionState() {
        viewModelScope.launch(errorHandler) {
            connectionState.value = internetConnectionUseCase()
            internetConnectionObserver.connectivityFlow.collectLatest {
                if (connectionState.value == InternetConnectionState.LOST
                    && it == InternetConnectionState.CONNECTED
                ) {
                    onLoadChannelData()
                    _vmEvents.trySend(ChannelDetailsVmEvent.RefreshVideosIfEmpty)
                }
                connectionState.value = it
            }
        }
    }

    private fun sendEvent(vmEvent: ChannelDetailsVmEvent) {
        _vmEvents.trySend(vmEvent)
    }
}