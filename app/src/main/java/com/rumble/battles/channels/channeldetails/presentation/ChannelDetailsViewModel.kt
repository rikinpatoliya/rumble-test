package com.rumble.battles.channels.channeldetails.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.analytics.CardSize
import com.rumble.analytics.LocalsJoinButtonEvent
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.battles.navigation.RumblePath
import com.rumble.domain.analytics.domain.domainmodel.channelDetailsScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.UploadVideoEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.LocalsCommunityEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelVideosUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.LogChannelViewUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.ReportChannelUseCase
import com.rumble.domain.common.domain.usecase.ShareUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.network.connection.InternetConnectionState
import com.rumble.network.session.SessionManager
import com.rumble.utils.extension.getChannelId
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.ReportType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ChannelDetailsHandler {

    val uiState: StateFlow<ChannelDetailsUIState>
    val listToggleViewStyle: Flow<ListToggleViewStyle>
    val popupState: StateFlow<ChannelDetailsDialog>
    val alertDialogState: StateFlow<AlertDialogState>
    val videoList: Flow<PagingData<Feed>>
    val updatedEntity: StateFlow<VideoEntity?>
    val vmEvents: Flow<ChannelDetailsVmEvent>
    val currentPlayerState: State<RumblePlayer?>
    val soundState: Flow<Boolean>

    fun onToggleVideoViewStyle(listToggleViewStyle: ListToggleViewStyle)
    fun onLike(videoEntity: VideoEntity)
    fun onDislike(videoEntity: VideoEntity)
    fun onVideoClick(feed: Feed)
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity)
    fun onFullyVisibleFeedChanged(feed: Feed?)
    fun onCreatePlayerForVisibleFeed()
    fun onPauseCurrentPlayer()
    fun onViewResumed()
    fun onSoundClick()
    fun onPlayerImpression(videoEntity: VideoEntity)

    //region CHANNEL ACTIONS
    fun onJoin(localsCommunityEntity: LocalsCommunityEntity)
    fun onActionMenuClicked()
    fun onBlockMenuClicked()
    fun onReportMenuClicked()
    fun onReport(reason: ReportType)
    fun onDismissDialog()
    fun onShareChannel()

    //endregion
    fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize)

    fun updateChannelDetailsEntity(channelDetailsEntity: ChannelDetailsEntity)
}

interface NotificationsScreenHandler {
    val uiState: StateFlow<ChannelDetailsUIState>
    val vmEvents: Flow<ChannelDetailsVmEvent>

    fun updateChannelDetailsEntity(channelDetailsEntity: ChannelDetailsEntity)
}

data class ChannelDetailsUIState(
    val channelId: String,
    val channelDetailsEntity: ChannelDetailsEntity? = null,
    val userUploadChannels: List<UserUploadChannelEntity>? = null,
    val loading: Boolean = false,
    val connectionState: InternetConnectionState = InternetConnectionState.CONNECTED,
    val userId: String = "",
    val userName: String = "",
    val userPicture: String = "",
    val shareAvailable: Boolean = false
)

sealed class ChannelDetailsDialog {
    data class BlockDialog(val channelDetailsEntity: ChannelDetailsEntity) : ChannelDetailsDialog()
    object ReportDialog : ChannelDetailsDialog()
    object ActionMenuDialog : ChannelDetailsDialog()
    data class LocalsPopupDialog(val localsCommunityEntity: LocalsCommunityEntity) :
        ChannelDetailsDialog()
}

sealed class ChannelDetailsAlertDialogReason : AlertDialogReason {
    object SendEmailErrorDialog : ChannelDetailsAlertDialogReason()
    data class RestrictedContentReason(val videoEntity: VideoEntity) :
        ChannelDetailsAlertDialogReason()
}

sealed class ChannelDetailsVmEvent {
    object ShowMenuPopup : ChannelDetailsVmEvent()
    object ShowLocalsPopup : ChannelDetailsVmEvent()
    object ShowChannelReportedMessage : ChannelDetailsVmEvent()
    object ShowEmailVerifiedMessage : ChannelDetailsVmEvent()
    data class ShowMoreUploadOptionsBottomSheet(val uploadVideoEntity: UploadVideoEntity) :
        ChannelDetailsVmEvent()

    data class Error(val errorMessage: String? = null) : ChannelDetailsVmEvent()
    data class PlayVideo(val videoEntity: VideoEntity) : ChannelDetailsVmEvent()
    object OpenAuthMenu: ChannelDetailsVmEvent()
}

private const val TAG = "ChannelDetailsViewModel"

@HiltViewModel
class ChannelDetailsViewModel @Inject constructor(
    private val getChannelDataUseCase: GetChannelDataUseCase,
    private val logChannelViewUseCase: LogChannelViewUseCase,
    getChannelVideosUseCase: GetChannelVideosUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val stateHandle: SavedStateHandle,
    private val reportChannelUseCase: ReportChannelUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase,
    private val getLastPositionUseCase: GetLastPositionUseCase,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase,
    private val shareUseCase: ShareUseCase,
    private val sessionManager: SessionManager,
) : ViewModel(), ChannelDetailsHandler, NotificationsScreenHandler {

    private var currentVisibleFeed: VideoEntity? = null
    private var lastDisplayedFeed: VideoEntity? = null
    private var isUserLoggedIn: Boolean = false
    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(ChannelDetailsVmEvent.Error())
    }

    override val uiState = MutableStateFlow(createInitialState())

    override val listToggleViewStyle: Flow<ListToggleViewStyle> =
        userPreferenceManager.channelDetailsListToggleViewStyle

    override val popupState =
        MutableStateFlow<ChannelDetailsDialog>(ChannelDetailsDialog.ActionMenuDialog)

    override val alertDialogState = MutableStateFlow(AlertDialogState())

    override val videoList: Flow<PagingData<Feed>> =
        getChannelVideosUseCase(uiState.value.channelId).cachedIn(viewModelScope)

    override val updatedEntity: MutableStateFlow<VideoEntity?> = MutableStateFlow(null)

    private val _vmEvents = Channel<ChannelDetailsVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<ChannelDetailsVmEvent> = _vmEvents.receiveAsFlow()

    override val currentPlayerState: MutableState<RumblePlayer?> = mutableStateOf(null)

    override val soundState = userPreferenceManager.videoCardSoundStateFlow

    init {
        observeLoginState()
        observeSoundState()
    }

    private fun loadChannelDetails() {
        viewModelScope.launch(errorHandler) {
            uiState.update { it.copy(loading = true) }
            getChannelDataUseCase(uiState.value.channelId)
                .onSuccess { channelDetailEntity ->
                    // Only log the view on the first load
                    if (uiState.value.channelDetailsEntity == null) {
                        logChannelViewUseCase(channelDetailEntity.channelId)
                    }
                    uiState.update {
                        it.copy(
                            channelDetailsEntity = channelDetailEntity,
                            loading = false,
                            shareAvailable = channelDetailEntity.channelUrl.isNullOrEmpty().not()
                        )
                    }
                }
                .onFailure { throwable ->
                    handleFailure(throwable)
                }
        }
    }

    override fun onToggleVideoViewStyle(listToggleViewStyle: ListToggleViewStyle) {
        viewModelScope.launch(errorHandler) {
            userPreferenceManager.saveChannelDetailsListToggleViewStyle(listToggleViewStyle)
            if (listToggleViewStyle == ListToggleViewStyle.LIST)
                onPauseCurrentPlayer()
        }
    }

    override fun onLike(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.LIKE)
            if (result.success) updateVotedVideoEntity(result.updatedFeed)
        }
    }

    override fun onDislike(videoEntity: VideoEntity) {
        viewModelScope.launch(errorHandler) {
            val result = voteVideoUseCase(videoEntity, UserVote.DISLIKE)
            if (result.success) updateVotedVideoEntity(result.updatedFeed)
        }
    }

    override fun onVideoClick(feed: Feed) {
        (feed as? VideoEntity)?.let { videoEntity ->
            if (videoEntity.ageRestricted) {
                alertDialogState.value = AlertDialogState(
                    true,
                    ChannelDetailsAlertDialogReason.RestrictedContentReason(videoEntity)
                )
            } else {
                onWatchVideo(videoEntity)
            }
        }
    }

    override fun onCancelRestricted() {
        alertDialogState.value = AlertDialogState()
        analyticsEventUseCase(MatureContentCancelEvent)
    }

    override fun onWatchRestricted(videoEntity: VideoEntity) {
        alertDialogState.value = AlertDialogState()
        onWatchVideo(videoEntity)
        analyticsEventUseCase(MatureContentWatchEvent)
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
                    currentPlayerState.value = initVideoCardPlayerUseCase(it, channelDetailsScreen)
                    lastDisplayedFeed = currentVisibleFeed
                }
            }
        }
    }

    override fun onPauseCurrentPlayer() {
        currentPlayerState.value?.pauseVideo()
    }

    override fun onViewResumed() {
        viewModelScope.launch {
            currentVisibleFeed?.id?.let {
                currentPlayerState.value?.let { player ->
                    if (player.currentVideoAgeRestricted.not() && listToggleViewStyle.first() == ListToggleViewStyle.GRID) {
                        player.seekTo(getLastPositionUseCase(it))
                        player.playVideo()
                    }
                }
            }

            loadChannelDetails()
        }
    }

    override fun onPlayerImpression(videoEntity: VideoEntity) {
        viewModelScope.launch {
            logVideoPlayerImpressionUseCase(
                screenId = channelDetailsScreen,
                index = videoEntity.index,
                cardSize = CardSize.REGULAR
            )
        }
    }

    override fun onSoundClick() {
        viewModelScope.launch {
            userPreferenceManager.saveVideoCardSoundState(soundState.first().not())
        }
    }

    //region CHANNEL ACTIONS
    override fun onJoin(localsCommunityEntity: LocalsCommunityEntity) {
        analyticsEventUseCase(
            LocalsJoinButtonEvent(
                screenId = channelDetailsScreen,
                creatorId = uiState.value.channelId.getChannelId(),
            )
        )
        popupState.value = ChannelDetailsDialog.LocalsPopupDialog(localsCommunityEntity)
        emitVmEvent(ChannelDetailsVmEvent.ShowLocalsPopup)
    }

    override fun onActionMenuClicked() {
        popupState.value = ChannelDetailsDialog.ActionMenuDialog
        emitVmEvent(ChannelDetailsVmEvent.ShowMenuPopup)
    }

    override fun onBlockMenuClicked() {
        if (isUserLoggedIn) {
            uiState.value.channelDetailsEntity?.let {
                popupState.value = ChannelDetailsDialog.BlockDialog(it)
                emitVmEvent(ChannelDetailsVmEvent.ShowMenuPopup)
            }
        } else {
            emitVmEvent(ChannelDetailsVmEvent.OpenAuthMenu)
        }
    }

    override fun onReportMenuClicked() {
        popupState.value = ChannelDetailsDialog.ReportDialog
        emitVmEvent(ChannelDetailsVmEvent.ShowMenuPopup)
    }

    override fun onReport(reason: ReportType) {
        uiState.value.channelDetailsEntity?.let { channelDetailsEntity ->
            viewModelScope.launch(errorHandler) {
                val success = reportChannelUseCase(
                    channelDetailsEntity = channelDetailsEntity,
                    reportType = reason
                )
                if (success) {
                    emitVmEvent(event = ChannelDetailsVmEvent.ShowChannelReportedMessage)
                } else {
                    emitVmEvent(event = ChannelDetailsVmEvent.Error())
                }
            }
        }
    }

    override fun onShareChannel() {
        uiState.value.channelDetailsEntity?.channelUrl?.let {
            shareUseCase(text = it)
        }
    }

    override fun onDismissDialog() {
        alertDialogState.value = AlertDialogState()
    }
    //endregion

    override fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize) {
        viewModelScope.launch(errorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = videoEntity.videoLogView.view,
                screenId = channelDetailsScreen,
                index = videoEntity.index,
                cardSize = cardSize
            )
        }
    }

    //region NOTIFICATIONS HANDLER ACTIONS
    override fun updateChannelDetailsEntity(channelDetailsEntity: ChannelDetailsEntity) {
        uiState.update {
            it.copy(
                channelDetailsEntity = channelDetailsEntity
            )
        }
    }

    //endregion

    private fun handleFailure(throwable: Throwable) {
        uiState.update { it.copy(loading = false) }
        unhandledErrorUseCase(TAG, throwable)
        emitVmEvent(ChannelDetailsVmEvent.Error())
    }

    private fun emitVmEvent(event: ChannelDetailsVmEvent) {
        _vmEvents.trySend(event)
    }

    private fun createInitialState() = ChannelDetailsUIState(
        channelId = stateHandle.get<String>(RumblePath.CHANNEL.path) ?: "",
    )

    private fun updateVotedVideoEntity(
        updatedFeed: VideoEntity
    ) {
        updatedEntity.value = updatedFeed
        uiState.value.channelDetailsEntity?.let { channelDetailsEntity ->
            channelDetailsEntity.featuredVideo?.let { featuredVideo ->
                if (featuredVideo.id == updatedFeed.id) {
                    uiState.update {
                        it.copy(
                            channelDetailsEntity = channelDetailsEntity.copy(
                                featuredVideo = featuredVideo.copy(
                                    userVote = updatedFeed.userVote,
                                    likeNumber = updatedFeed.likeNumber,
                                    dislikeNumber = updatedFeed.dislikeNumber
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    private fun observeLoginState() {
        viewModelScope.launch {
            sessionManager.cookiesFlow.distinctUntilChanged().collectLatest {
                isUserLoggedIn = it.isNotEmpty()
            }
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

    private fun onWatchVideo(videoEntity: VideoEntity) {
        currentPlayerState.value?.currentPositionValue?.let { position ->
            currentVisibleFeed?.id?.let { videoId ->
                if (videoEntity.id == videoId) {
                    viewModelScope.launch {
                        if (soundState.first()) saveLastPositionUseCase(position, videoId)
                    }
                }
            }
        }
        emitVmEvent(ChannelDetailsVmEvent.PlayVideo(videoEntity))
    }
}