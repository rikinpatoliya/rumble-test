package com.rumble.player

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.domainmodel.videoDetailsScreen
import com.rumble.domain.analytics.domain.usecases.LogRumbleVideoUseCase
import com.rumble.domain.analytics.domain.usecases.LogVideoDetailsUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.GetVideoDetailsUseCase
import com.rumble.domain.feed.domain.usecase.ReportContentUseCase
import com.rumble.domain.feed.domain.usecase.StartPremiumPreviewCountdownUseCase
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.landing.usecases.GetUserCookiesUseCase
import com.rumble.domain.livechat.domain.usecases.CalculateLiveGateCountdownValueUseCase
import com.rumble.domain.premium.domain.usecases.FetchUserInfoUseCase
import com.rumble.domain.settings.domain.usecase.HasPremiumRestrictionUseCase
import com.rumble.domain.video.domain.usecases.CreateRumblePlayListUseCase
import com.rumble.domain.video.domain.usecases.InitVideoPlayerSourceUseCase
import com.rumble.domain.video.domain.usecases.SaveLastPositionUseCase
import com.rumble.domain.video.domain.usecases.UpdateVideoPlayerSourceUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import com.rumble.network.dto.LiveStreamStatus
import com.rumble.network.dto.channel.ReportContentType
import com.rumble.network.session.SessionManager
import com.rumble.videoplayer.domain.model.VoteData
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.CountDownType
import com.rumble.videoplayer.player.config.LiveVideoReportResult
import com.rumble.videoplayer.player.config.ReportType
import com.rumble.videoplayer.player.config.VideoScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "VideoPlaybackViewModel"

data class VideoPlayerState(
    val rumblePlayer: RumblePlayer? = null,
    val videoEntity: VideoEntity? = null,
    val currentVote: VoteData = VoteData.NONE,
    val fromChannel: String = "",
    val channelDetailsEntity: CreatorEntity? = null,
    val showPayWall: Boolean = false,
)

sealed class VideoPlayerEvent {
    data object VideoReported : VideoPlayerEvent()
    data object Error : VideoPlayerEvent()
    data class LoginToLike(val errorMessage: String?) : VideoPlayerEvent()
    data class LoginToDislike(val errorMessage: String?) : VideoPlayerEvent()
    data object ClosePlayer : VideoPlayerEvent()
    data object LoginToAddToPlaylist : VideoPlayerEvent()
    data object AddToPlaylist : VideoPlayerEvent()
    data class OpenChannelDetails(val channelDetailsEntity: CreatorEntity) :
        VideoPlayerEvent()
}

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    private val getVideoDetailsUseCase: GetVideoDetailsUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val initVideoPlayerSourceUseCase: InitVideoPlayerSourceUseCase,
    private val logVideoDetailsUseCase: LogVideoDetailsUseCase,
    private val logRumbleVideoUseCase: LogRumbleVideoUseCase,
    private val reportContentUseCase: ReportContentUseCase,
    private val saveLastPositionUseCase: SaveLastPositionUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
    private val internetConnectionUseCase: InternetConnectionUseCase,
    private val voteVideoUseCase: VoteVideoUseCase,
    private val getChannelDataUseCase: GetChannelDataUseCase,
    private val updateVideoPlayerSourceUseCase: UpdateVideoPlayerSourceUseCase,
    private val getUserCookiesUseCase: GetUserCookiesUseCase,
    private val createRumblePlayListUseCase: CreateRumblePlayListUseCase,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
    private val sessionManager: SessionManager,
    private val hasPremiumRestrictionUseCase: HasPremiumRestrictionUseCase,
    private val calculateLiveGateCountdownValueUseCase: CalculateLiveGateCountdownValueUseCase,
    private val startPremiumPreviewCountdownUseCase: StartPremiumPreviewCountdownUseCase,
) : ViewModel() {

    val videoPlayerState: MutableState<VideoPlayerState> = mutableStateOf(VideoPlayerState())
    val eventFlow: MutableSharedFlow<VideoPlayerEvent> = MutableSharedFlow()
    val connectionState: MutableLiveData<InternetConnectionState> =
        MutableLiveData<InternetConnectionState>()

    private var isPremium: Boolean = false
    private var connectionStateJob: Job = Job()
    private var playerImpressionLogged = false
    private var videoReady: Boolean = false

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    init {
        observeConnectionState()
    }

    fun initPlayerWithVideo(videoEntity: VideoEntity, fromChannel: String) {
        viewModelScope.launch(errorHandler) {
            fetchUserInfoUseCase()
            isPremium = sessionManager.isPremiumUserFlow.first()
            val updatedVideoEntity = getVideoDetailsUseCase(videoEntity.id) ?: videoEntity
            videoPlayerState.value = videoPlayerState.value.copy(
                rumblePlayer = initVideoPlayerSourceUseCase(
                    videoId = updatedVideoEntity.id,
                    screenId = videoDetailsScreen,
                    saveLastPosition = saveLastPositionUseCase::invoke,
                    autoplay = updatedVideoEntity.hasLiveGate.not() || hasPremiumRestrictionUseCase(videoEntity).not(),
                    requestLiveGateData = true,
                    videoScope = VideoScope.VideoDetails,
                    onNextVideo = { videoId, channelId, autoPlay ->
                        onNextVideo(videoId, channelId, autoPlay, true)
                    },
                    showAds = sessionManager.isPremiumUserFlow.first().not(),
                    liveVideoReport = { videoId, result ->
                        onLiveVideoReport(videoId, result)
                    },
                    onPremiumCountdownFinished = {
                        enforceLiveGateRestriction()
                    },
                    onVideoReady = { duration, player ->
                        handleLiveGate(updatedVideoEntity, duration, player)
                    }
                ),
                videoEntity = updatedVideoEntity,
                fromChannel = fromChannel,
                channelDetailsEntity = fetchChannelDetails(updatedVideoEntity.channelId),
                showPayWall = (updatedVideoEntity.hasLiveGate.not() &&
                    hasPremiumRestrictionUseCase(updatedVideoEntity)) ||
                    (updatedVideoEntity.hasLiveGate &&
                        hasPremiumRestrictionUseCase(updatedVideoEntity) &&
                        updatedVideoEntity.livestreamStatus == LiveStreamStatus.LIVE)
            )
            if (videoPlayerState.value.showPayWall.not()) {
                videoPlayerState.value.rumblePlayer?.playVideo()
            }
            fetchCurrentUserVoteState(videoId = videoEntity.id)
            onVideoPlayerImpression()
        }
    }

    fun initPlayerWithVideoList(title: String, videoList: List<VideoEntity>, shuffle: Boolean) {
        viewModelScope.launch(errorHandler) {
            val playList = createRumblePlayListUseCase(
                title = title,
                feedList = videoList,
                shuffle = shuffle,
                loop = true,
                requestLiveGateData = true,
                applyLastPosition = false
            )
            val initialVideo = videoList.first()
            val updatedVideoEntity = getVideoDetailsUseCase(initialVideo.id) ?: initialVideo
            fetchUserInfoUseCase()
            isPremium = sessionManager.isPremiumUserFlow.first()

            videoPlayerState.value = videoPlayerState.value.copy(
                rumblePlayer = initVideoPlayerSourceUseCase(
                    playList = playList,
                    screenId = videoDetailsScreen,
                    saveLastPosition = saveLastPositionUseCase::invoke,
                    liveVideoReport = { videoId, result ->
                        onLiveVideoReport(videoId, result)
                    },
                    onNextVideo = { videoId, channelId, autoPlay ->
                        onNextVideo(videoId, channelId, autoPlay, false)
                    },
                    showAds = sessionManager.isPremiumUserFlow.first().not(),
                    onPremiumCountdownFinished = {
                        enforceLiveGateRestriction()
                    },
                    onVideoReady = { duration, player ->
                        handleLiveGate(updatedVideoEntity, duration, player)
                    }
                ),
                videoEntity = updatedVideoEntity,
                channelDetailsEntity = fetchChannelDetails(updatedVideoEntity.channelId),
                showPayWall = hasPremiumRestrictionUseCase(updatedVideoEntity)
            )
            if (videoPlayerState.value.showPayWall.not()) {
                videoPlayerState.value.rumblePlayer?.playVideo()
            }
            fetchCurrentUserVoteState(videoId = updatedVideoEntity.id)
            onVideoPlayerImpression()
        }
    }

    override fun onCleared() {
        videoPlayerState.value.rumblePlayer?.stopPlayer()
    }

    fun onReport(reportType: ReportType) {
        videoPlayerState.value.rumblePlayer?.videoId?.let {
            viewModelScope.launch(errorHandler) {
                val success = reportContentUseCase(
                    contentId = it,
                    reportType = reportType,
                    contentReportType = ReportContentType.VIDEO
                )
                if (success) eventFlow.emit(VideoPlayerEvent.VideoReported)
                else eventFlow.emit(VideoPlayerEvent.Error)
            }
        }
    }

    fun onLikeVideo() {
        viewModelScope.launch(errorHandler) {
            videoPlayerState.value.videoEntity?.let {
                val result = voteVideoUseCase(it, UserVote.LIKE)
                if (result.success) {
                    videoPlayerState.value = videoPlayerState.value.copy(
                        currentVote = getUserCurrentVote(result.updatedFeed),
                        videoEntity = result.updatedFeed
                    )
                } else eventFlow.emit(VideoPlayerEvent.LoginToLike(result.errorMessage))
            }
        }
    }

    fun onDislikeVideo() {
        viewModelScope.launch(errorHandler) {
            videoPlayerState.value.videoEntity?.let {
                val result = voteVideoUseCase(it, UserVote.DISLIKE)
                if (result.success) {
                    videoPlayerState.value = videoPlayerState.value.copy(
                        currentVote = getUserCurrentVote(result.updatedFeed),
                        videoEntity = result.updatedFeed
                    )
                } else eventFlow.emit(VideoPlayerEvent.LoginToDislike(result.errorMessage))
            }
        }
    }

    fun onAddToPlaylist() {
        viewModelScope.launch(errorHandler) {
            val notLoggedIn = getUserCookiesUseCase().isEmpty()
            if (notLoggedIn) {
                eventFlow.emit(VideoPlayerEvent.LoginToAddToPlaylist)
            } else {
                eventFlow.emit(VideoPlayerEvent.AddToPlaylist)
            }
        }
    }

    fun onChannelDetails() {
        viewModelScope.launch(errorHandler) {
            if (videoPlayerState.value.fromChannel.isNotEmpty()
                && (videoPlayerState.value.channelDetailsEntity?.channelId
                    ?: "") == videoPlayerState.value.fromChannel
            ) {
                eventFlow.emit(VideoPlayerEvent.ClosePlayer)
            } else {
                videoPlayerState.value.channelDetailsEntity?.let {
                    eventFlow.emit(VideoPlayerEvent.OpenChannelDetails(it))
                } ?: run {
                    eventFlow.emit(VideoPlayerEvent.Error)
                }
            }
        }
    }

    fun onViewPaused() {
        videoPlayerState.value.rumblePlayer?.pauseVideo()
    }

    private fun enforceLiveGateRestriction() {
        videoPlayerState.value.videoEntity?.let {
            viewModelScope.launch(Dispatchers.Main + errorHandler) {
                if (hasPremiumRestrictionUseCase(it)) {
                    videoPlayerState.value.rumblePlayer?.pauseVideo()
                    videoPlayerState.value =
                        videoPlayerState.value.copy(showPayWall = true)
                }
            }
        }
    }

    private fun handleLiveGate(videoEntity: VideoEntity, actualDuration: Long, player: RumblePlayer) {
        if (videoReady.not()) {
            videoReady = true
            viewModelScope.launch(errorHandler) {
                val hasRestriction = hasPremiumRestrictionUseCase(videoEntity)
                if (hasRestriction) {
                    videoEntity.liveGateEntity?.let {
                        videoPlayerState.value = videoPlayerState.value.copy(
                            videoEntity = videoEntity.copy(hasLiveGate = true),
                            rumblePlayer = player,
                        )
                        if (videoEntity.livestreamStatus != LiveStreamStatus.LIVE) {
                            startPremiumPreviewCountdownUseCase(player, actualDuration)
                        } else {
                            enforceLiveGateRestriction()
                        }
                    }

                }
                videoPlayerState.value.rumblePlayer?.userIsPremium = hasRestriction.not()
            }
        }
    }

    private fun onLiveVideoReport(videoId: Long, result: LiveVideoReportResult) {
        if (result.statusCode != videoPlayerState.value.videoEntity?.livestreamStatus?.value &&
            videoId == videoPlayerState.value.videoEntity?.id
        ) {
            updateVideoSource(
                videoId = videoId,
                updatedRelatedVideoList = true,
                autoplay = false,
                applyLastPosition = false
            )
        }
        if (result.hasLiveGate && videoPlayerState.value.videoEntity?.hasLiveGate == false) {
            videoPlayerState.value = videoPlayerState.value.copy(
                videoEntity = videoPlayerState.value.videoEntity?.copy(hasLiveGate = true)
            )
            videoPlayerState.value.videoEntity?.let {
                val countdown = calculateLiveGateCountdownValueUseCase(
                    it,
                    result.videoTimeCode ?: 0,
                    result.countDownValue ?: 0
                )
                videoPlayerState.value.rumblePlayer?.startPremiumCountDown(
                    countdown.toLong(),
                    CountDownType.Premium
                )
            }
        }
    }

    private fun updateVideoSource(
        videoId: Long,
        updatedRelatedVideoList: Boolean,
        autoplay: Boolean,
        applyLastPosition: Boolean
    ) {
        viewModelScope.launch(errorHandler) {
            val videoEntityDeferred = async { getVideoDetailsUseCase(videoId) }
            videoEntityDeferred.await()?.let { videoEntity ->
                videoPlayerState.value = videoPlayerState.value.copy(
                    videoEntity = videoEntity,
                    currentVote = getUserCurrentVote(videoEntity),
                    showPayWall = hasPremiumRestrictionUseCase(videoEntity)
                )
                videoPlayerState.value.rumblePlayer?.let {
                    updateVideoPlayerSourceUseCase(
                        player = it,
                        videoEntity = videoEntity,
                        saveLastPosition = saveLastPositionUseCase::invoke,
                        screenId = videoDetailsScreen,
                        autoplay = autoplay,
                        videoScope = VideoScope.VideoDetails,
                        updatedRelatedVideoList = updatedRelatedVideoList,
                        requestLiveGateData = true,
                        applyLastPosition = applyLastPosition,
                        onPremiumCountdownFinished = {
                            enforceLiveGateRestriction()
                        },
                        onVideoReady = { duration, player ->
                            handleLiveGate(videoEntity, duration, player)
                        }
                    )
                }
                if (videoPlayerState.value.showPayWall.not()) {
                    videoPlayerState.value.rumblePlayer?.playVideo()
                }
            }
        }
    }

    private fun observeConnectionState() {
        connectionStateJob = viewModelScope.launch(errorHandler) {
            connectionState.value = internetConnectionUseCase()
            internetConnectionObserver.connectivityFlow.collectLatest {
                connectionState.value = it
            }
        }
    }

    private fun onNextVideo(
        videoId: Long,
        channelId: String,
        autoplay: Boolean,
        applyLastPosition: Boolean
    ) {
        playerImpressionLogged = false
        videoPlayerState.value.rumblePlayer?.pauseVideo()
        viewModelScope.launch(errorHandler) {
            videoPlayerState.value = videoPlayerState.value.copy(
                channelDetailsEntity = fetchChannelDetails(channelId),
            )
        }
        updateVideoSource(
            videoId = videoId,
            updatedRelatedVideoList = false,
            autoplay = autoplay,
            applyLastPosition = applyLastPosition
        )
        onVideoPlayerImpression()
    }

    private suspend fun fetchCurrentUserVoteState(videoId: Long) {
        getVideoDetailsUseCase(videoId)?.let {
            videoPlayerState.value = videoPlayerState.value.copy(
                currentVote = getUserCurrentVote(it)
            )
        }
    }

    private fun onError(throwable: Throwable) {
        unhandledErrorUseCase(TAG, throwable)
        viewModelScope.launch { eventFlow.emit(VideoPlayerEvent.Error) }
    }

    private fun onVideoPlayerImpression() {
        if (playerImpressionLogged.not()) {
            videoPlayerState.value.videoEntity?.let { videoEntity ->
                playerImpressionLogged = true
                viewModelScope.launch(errorHandler) {
                    logVideoDetailsUseCase(
                        videoDetailsScreen,
                        videoEntity.id.toString()
                    )
                    logRumbleVideoUseCase(
                        videoPath = videoEntity.videoLogView.view,
                        screenId = videoDetailsScreen
                    )
                }
            }
        }
    }

    private fun getUserCurrentVote(videoEntity: VideoEntity): VoteData =
        when (videoEntity.userVote) {
            UserVote.NONE -> VoteData.NONE
            UserVote.DISLIKE -> VoteData.DISLIKE
            UserVote.LIKE -> VoteData.LIKE
        }

    private suspend fun fetchChannelDetails(channelId: String): CreatorEntity? =
        viewModelScope.async { getChannelDataUseCase(channelId).getOrNull() }.await()
}