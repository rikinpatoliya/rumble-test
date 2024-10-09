package com.rumble.battles.videos.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rumble.analytics.CardSize
import com.rumble.analytics.MatureContentCancelEvent
import com.rumble.analytics.MatureContentWatchEvent
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsUIState
import com.rumble.battles.channels.channeldetails.presentation.ChannelDetailsVmEvent
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.domainmodel.myVideosScreen
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoCardImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoPlayerImpressionUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.UploadStatus
import com.rumble.domain.camera.UploadVideoEntity
import com.rumble.domain.camera.domain.usecases.CancelUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.DeleteUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.RestartUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.RestartWaitingConnectionVideoUploadsUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelVideosUseCase
import com.rumble.domain.channels.channeldetails.domain.usecase.GetUserUploadChannelsUseCase
import com.rumble.domain.common.domain.domainmodel.EmptyResult
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.profile.domain.GetUserProfileUseCase
import com.rumble.domain.profile.domainmodel.UserProfileEntity
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import com.rumble.domain.video.domain.usecases.InitVideoCardPlayerUseCase
import com.rumble.domain.video.domain.usecases.RequestEmailVerificationUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants.RETRY_DELAY_USER_UPLOAD_CHANNELS
import com.rumble.videoplayer.player.RumblePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MyVideosHandler {

    val uiState: StateFlow<ChannelDetailsUIState>
    val listToggleViewStyle: Flow<ListToggleViewStyle>
    val videoList: Flow<PagingData<Feed>>
    val updatedEntity: StateFlow<VideoEntity?>
    val userUploadChannels: StateFlow<List<UserUploadChannelEntity>?>
    val userUploads: Flow<List<UploadVideoEntity>>
    val userSuccessfulUploads: Flow<List<UploadVideoEntity>>
    val vmEvents: Flow<ChannelDetailsVmEvent>
    val alertDialogState: StateFlow<AlertDialogState>
    val currentPlayerState: State<RumblePlayer?>
    val soundState: Flow<Boolean>

    fun onToggleVideoViewStyle(listToggleViewStyle: ListToggleViewStyle)
    fun onLike(videoEntity: VideoEntity)
    fun onDislike(videoEntity: VideoEntity)
    fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize)
    fun onMoreUploadOptionsClicked(uploadVideoEntity: UploadVideoEntity)
    fun onCancelUploadClicked(uploadVideoEntity: UploadVideoEntity)
    fun onDismissDialog()
    fun resendVerificationEmail()
    fun checkEmailValidationAndRetry(uploadVideoEntity: UploadVideoEntity)
    fun removeUploading(uploadVideoEntity: UploadVideoEntity)
    fun uploadOverCellular(uploadVideoEntity: UploadVideoEntity)
    fun retryUploading(uploadVideoEntity: UploadVideoEntity)
    fun onCancelUpload(uploadVideoEntity: UploadVideoEntity)
    fun onDeleteSuccededUploads(uploadVideoEntities: List<UploadVideoEntity>)
    fun onFullyVisibleFeedChanged(feed: Feed?)
    fun onCreatePlayerForVisibleFeed()
    fun onViewPaused()
    fun onViewResumed()
    fun onSoundClick()
    fun onVideoClick(videoEntity: VideoEntity)
    fun onPlayerImpression(videoEntity: VideoEntity)
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity)
}

sealed class MyVideosScreenAlertDialogReason : AlertDialogReason {
    data class CancelUploadDialogReason(val uploadVideoEntity: UploadVideoEntity) :
        MyVideosScreenAlertDialogReason()

    data class ShowEmailVerificationSent(val email: String) : MyVideosScreenAlertDialogReason()
    object ShowYourEmailNotVerifiedYet : MyVideosScreenAlertDialogReason()
    data class RestrictedContentReason(val videoEntity: VideoEntity) :
        MyVideosScreenAlertDialogReason()
}

private const val TAG = "MyVideosViewModel"

@HiltViewModel
class MyVideosViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    getChannelDataUseCase: GetChannelDataUseCase,
    getChannelVideosUseCase: GetChannelVideosUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val logVideoCardImpressionUseCase: LogVideoCardImpressionUseCase,
    private val getUserUploadChannelsUseCase: GetUserUploadChannelsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val requestEmailVerificationUseCase: RequestEmailVerificationUseCase,
    private val initVideoCardPlayerUseCase: InitVideoCardPlayerUseCase,
    private val getLastPositionUseCase: GetLastPositionUseCase,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val logVideoPlayerImpressionUseCase: LogVideoPlayerImpressionUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    getUploadVideoUseCase: GetUploadVideoUseCase,
    private val cancelUploadVideoUseCase: CancelUploadVideoUseCase,
    private val restartUploadVideoUseCase: RestartUploadVideoUseCase,
    private val deleteUploadVideoUseCase: DeleteUploadVideoUseCase,
    private val restartWaitingConnectionVideoUploadsUseCase: RestartWaitingConnectionVideoUploadsUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
    private val internetConnectionUseCase: InternetConnectionUseCase,
) : ViewModel(), MyVideosHandler {

    private var currentVisibleFeed: VideoEntity? = null
    private var lastDisplayedFeed: VideoEntity? = null

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        handleFailure(throwable)
    }

    override val uiState = MutableStateFlow(ChannelDetailsUIState(channelId = ""))

    override val listToggleViewStyle: Flow<ListToggleViewStyle> =
        userPreferenceManager.myVideosListToggleViewStyle

    override val updatedEntity: MutableStateFlow<VideoEntity?> = MutableStateFlow(null)

    override val userUploadChannels: MutableStateFlow<List<UserUploadChannelEntity>?> =
        MutableStateFlow(null)

    override val userUploads: Flow<List<UploadVideoEntity>> =
        getUploadVideoUseCase().map { uploadList ->
            uploadList.filterNot {
                it.status == UploadStatus.DRAFT || it.status == UploadStatus.UPLOADING_SUCCEEDED
            }
        }

    override val userSuccessfulUploads: Flow<List<UploadVideoEntity>> =
        getUploadVideoUseCase().map { uploadList ->
            uploadList.filter { it.status == UploadStatus.UPLOADING_SUCCEEDED }
        }

    private val _vmEvents = Channel<ChannelDetailsVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<ChannelDetailsVmEvent> = _vmEvents.receiveAsFlow()

    override val alertDialogState = MutableStateFlow(AlertDialogState())

    override val currentPlayerState: MutableState<RumblePlayer?> = mutableStateOf(null)

    override val soundState = userPreferenceManager.videoCardSoundStateFlow

    init {
        viewModelScope.launch(errorHandler) {
            uiState.update { it.copy(loading = true, userId = sessionManager.userIdFlow.first()) }
            fetchUserUploadChannels()
            getChannelDataUseCase(uiState.value.userId)
                .onSuccess { channelDetailEntity ->
                    uiState.update {
                        it.copy(
                            channelDetailsEntity = channelDetailEntity,
                            loading = false
                        )
                    }
                }
                .onFailure { throwable ->
                    handleFailure(throwable)
                }
        }
        startObserveConnectionState()
        observeUserNamePicture()
        observeSoundState()
        observePlaybackInFeedMode()
        resetWaitingConnectionUploads()
    }

    override val videoList: Flow<PagingData<Feed>> =
        getChannelVideosUseCase(uiState.value.userId).cachedIn(viewModelScope)

    override fun onToggleVideoViewStyle(listToggleViewStyle: ListToggleViewStyle) {
        viewModelScope.launch(errorHandler) {
            userPreferenceManager.saveMyVideosListToggleViewStyle(listToggleViewStyle)
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

    override fun onVideoCardImpression(videoEntity: VideoEntity, cardSize: CardSize) {
        viewModelScope.launch(errorHandler) {
            logVideoCardImpressionUseCase(
                videoPath = videoEntity.videoLogView.view,
                screenId = myVideosScreen,
                index = videoEntity.index,
                cardSize = cardSize
            )
        }
    }

    override fun onMoreUploadOptionsClicked(uploadVideoEntity: UploadVideoEntity) {
        emitVmEvent(ChannelDetailsVmEvent.ShowMoreUploadOptionsBottomSheet(uploadVideoEntity))
    }

    override fun onCancelUploadClicked(uploadVideoEntity: UploadVideoEntity) {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = MyVideosScreenAlertDialogReason.CancelUploadDialogReason(
                uploadVideoEntity
            )
        )
    }

    override fun onDismissDialog() {
        alertDialogState.value = AlertDialogState()
    }

    override fun resendVerificationEmail() {
        viewModelScope.launch(errorHandler) {
            val profile = fetchUserProfile()
            profile?.email?.let {
                when (requestEmailVerificationUseCase(it)) {
                    is EmptyResult.Failure -> handleError()
                    EmptyResult.Success -> showEmailVerificationSent(it)
                }
            }
        }
    }

    override fun checkEmailValidationAndRetry(uploadVideoEntity: UploadVideoEntity) {
        viewModelScope.launch(errorHandler) {
            val profile = fetchUserProfile()
            profile?.validated?.let { validated ->
                if (validated) {
                    emitVmEvent(ChannelDetailsVmEvent.ShowEmailVerifiedMessage)
                    retryUploading(uploadVideoEntity)
                } else {
                    showEmailVerificationFailure()
                }
            }
        }
    }

    override fun removeUploading(uploadVideoEntity: UploadVideoEntity) {
        onCancelUploadClicked(uploadVideoEntity)
    }

    override fun uploadOverCellular(uploadVideoEntity: UploadVideoEntity) {
        viewModelScope.launch(errorHandler) {
            restartUploadVideoUseCase(uploadVideoEntity.uuid, true)
        }
    }

    override fun retryUploading(uploadVideoEntity: UploadVideoEntity) {
        viewModelScope.launch(errorHandler) {
            restartUploadVideoUseCase(uploadVideoEntity.uuid)
        }
    }

    override fun onCancelUpload(uploadVideoEntity: UploadVideoEntity) {
        onDismissDialog()
        viewModelScope.launch(errorHandler) {
            cancelUploadVideoUseCase(uploadVideoEntity.uuid)
        }
    }

    override fun onDeleteSuccededUploads(uploadVideoEntities: List<UploadVideoEntity>) {
        viewModelScope.launch {
            uploadVideoEntities.forEach {
                deleteUploadVideoUseCase(it.uuid)
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
                    currentPlayerState.value = initVideoCardPlayerUseCase(it, myVideosScreen)
                    lastDisplayedFeed = currentVisibleFeed
                }
            }
        }
    }

    override fun onViewPaused() {
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

    override fun onSoundClick() {
        viewModelScope.launch {
            userPreferenceManager.saveVideoCardSoundState(soundState.first().not())
        }
    }

    override fun onVideoClick(videoEntity: VideoEntity) {
        if (videoEntity.ageRestricted) {
            alertDialogState.value = AlertDialogState(
                true,
                MyVideosScreenAlertDialogReason.RestrictedContentReason(videoEntity)
            )
        } else {
            onWatchVideo(videoEntity)
        }
    }

    override fun onPlayerImpression(videoEntity: VideoEntity) {
        viewModelScope.launch {
            logVideoPlayerImpressionUseCase(
                screenId = myVideosScreen,
                index = videoEntity.index,
                cardSize = CardSize.REGULAR
            )
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

    private fun observeUserNamePicture() {
        viewModelScope.launch {
            sessionManager.userNameFlow.collect {
                uiState.value = uiState.value.copy(userName = it)
            }
        }
        viewModelScope.launch {
            sessionManager.userPictureFlow.collect {
                uiState.value = uiState.value.copy(userPicture = it)
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
        currentPlayerState.value?.stopPlayer()
        currentPlayerState.value = null
        lastDisplayedFeed = null
        emitVmEvent(ChannelDetailsVmEvent.PlayVideo(videoEntity))
    }

    private fun observeSoundState() {
        viewModelScope.launch {
            userPreferenceManager.videoCardSoundStateFlow.collectLatest { enabled ->
                if (enabled.not()) currentPlayerState.value?.mute()
                else currentPlayerState.value?.unMute()
            }
        }
    }

    private suspend fun fetchUserUploadChannels() {
        val resultDeferred = viewModelScope.async { getUserUploadChannelsUseCase() }
        when (val result = resultDeferred.await()) {
            is UserUploadChannelsResult.UserUploadChannelsError -> {
                delay(RETRY_DELAY_USER_UPLOAD_CHANNELS)
                fetchUserUploadChannels()
            }

            is UserUploadChannelsResult.UserUploadChannelsSuccess -> {
                userUploadChannels.value = result.userUploadChannels
            }
        }
    }

    private fun showEmailVerificationSent(email: String) {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = MyVideosScreenAlertDialogReason.ShowEmailVerificationSent(email)
        )
    }

    private fun showEmailVerificationFailure() {
        alertDialogState.value = AlertDialogState(
            show = true,
            alertDialogReason = MyVideosScreenAlertDialogReason.ShowYourEmailNotVerifiedYet
        )
    }

    private suspend fun fetchUserProfile(): UserProfileEntity? {
        val result = getUserProfileUseCase()
        return if (result.success) {
            result.userProfileEntity
        } else {
            handleError()
            null
        }
    }

    private fun emitVmEvent(event: ChannelDetailsVmEvent) {
        _vmEvents.trySend(event)
    }

    private fun handleFailure(throwable: Throwable) {
        uiState.update { it.copy(loading = false) }
        unhandledErrorUseCase(TAG, throwable)
        emitVmEvent(ChannelDetailsVmEvent.Error())
    }

    private fun handleError() {
        emitVmEvent(ChannelDetailsVmEvent.Error())
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
            uiState.update {
                it.copy(connectionState = internetConnectionUseCase())
            }
            internetConnectionObserver.connectivityFlow.collectLatest {
                if ((it == InternetConnectionState.CONNECTED) and (uiState.value.connectionState == InternetConnectionState.LOST)) {
                    resetWaitingConnectionUploads()
                }
                uiState.update { uiState ->
                    uiState.copy(connectionState = it)
                }
            }
        }
    }

    private fun resetWaitingConnectionUploads() {
        viewModelScope.launch(errorHandler) {
            restartWaitingConnectionVideoUploadsUseCase()
        }
    }
}