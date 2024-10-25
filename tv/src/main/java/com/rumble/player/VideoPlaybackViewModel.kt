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
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.GetVideoDetailsUseCase
import com.rumble.domain.feed.domain.usecase.ReportContentUseCase
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
import com.rumble.network.queryHelpers.PublisherId
import com.rumble.network.session.SessionManager
import com.rumble.videoplayer.domain.model.VoteData
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.CountDownType
import com.rumble.videoplayer.player.config.LiveVideoReportResult
import com.rumble.videoplayer.player.config.ReportType
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
    val channelDetailsEntity: ChannelDetailsEntity? = null,
    val showPayWall: Boolean = false,
    val showPayWallControls: Boolean = false,
)

sealed class VideoPlayerEvent {
    data object VideoReported : VideoPlayerEvent()
    data object Error : VideoPlayerEvent()
    data object LoginToLike : VideoPlayerEvent()
    data object LoginToDislike : VideoPlayerEvent()
    data object ClosePlayer : VideoPlayerEvent()
    data object LoginToAddToPlaylist : VideoPlayerEvent()
    data object AddToPlaylist : VideoPlayerEvent()
    data class OpenChannelDetails(val channelDetailsEntity: ChannelDetailsEntity) :
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
) : ViewModel() {

    val videoPlayerState: MutableState<VideoPlayerState> = mutableStateOf(VideoPlayerState())
    val eventFlow: MutableSharedFlow<VideoPlayerEvent> = MutableSharedFlow()
    val connectionState: MutableLiveData<InternetConnectionState> =
        MutableLiveData<InternetConnectionState>()

    private var isPremium: Boolean = false
    private var connectionStateJob: Job = Job()
    private var playerImpressionLogged = false

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
                    autoplay = true,
                    requestLiveGateData = true,
                    onNextVideo = ::onNextVideo,
                    showAds = sessionManager.isPremiumUserFlow.first().not(),
                    liveVideoReport = { videoId, result ->
                        onLiveVideoReport(videoId, result)
                    },
                    onPremiumCountdownFinished = {
                        enforceLiveGateRestriction()
                    }
                ),
                videoEntity = updatedVideoEntity,
                fromChannel = fromChannel,
                channelDetailsEntity = fetchChannelDetails(updatedVideoEntity.channelId),
                showPayWall = updatedVideoEntity.hasLiveGate.not() && hasPremiumRestrictionUseCase(
                    updatedVideoEntity
                )
            )
            if (videoPlayerState.value.showPayWall.not()) {
                videoPlayerState.value.rumblePlayer?.playVideo()
            }
            fetchCurrentUserVoteState(videoId = videoEntity.id)
            onVideoPlayerImpression()
            handleLiveGate(updatedVideoEntity)
        }
    }

    fun initPlayerWithVideoList(title: String, videoList: List<VideoEntity>, shuffle: Boolean) {
        viewModelScope.launch(errorHandler) {
            val playList = createRumblePlayListUseCase(
                title = title,
                feedList = videoList,
                publisherId = PublisherId.AndroidTv,
                shuffle = shuffle,
                loop = true,
                requestLiveGateData = true,
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
                    onNextVideo = ::onNextVideo,
                    showAds = sessionManager.isPremiumUserFlow.first().not(),
                    onPremiumCountdownFinished = {
                        enforceLiveGateRestriction()
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
            handleLiveGate(updatedVideoEntity)
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
                } else eventFlow.emit(VideoPlayerEvent.LoginToLike)
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
                } else eventFlow.emit(VideoPlayerEvent.LoginToDislike)
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

    private suspend fun handleLiveGate(videoEntity: VideoEntity) {
        val hasRestriction = hasPremiumRestrictionUseCase(videoEntity)
        if (hasRestriction) {
            videoEntity.liveGateEntity?.let {
                videoPlayerState.value = videoPlayerState.value.copy(
                    showPayWallControls = true,
                    videoEntity = videoPlayerState.value.videoEntity?.copy(hasLiveGate = true)
                )
                if (videoEntity.livestreamStatus != LiveStreamStatus.LIVE) {
                    videoPlayerState.value.rumblePlayer?.startPremiumCountDown(type = CountDownType.FreePreview)
                } else {
                    enforceLiveGateRestriction()
                }
            }

        } else if (videoEntity.liveGateEntity != null) {
            videoPlayerState.value = videoPlayerState.value.copy(
                showPayWallControls = true
            )
        }
        videoPlayerState.value.rumblePlayer?.userIsPremium = hasRestriction.not()
    }

    private fun onLiveVideoReport(videoId: Long, result: LiveVideoReportResult) {
        if (result.statusCode != videoPlayerState.value.videoEntity?.livestreamStatus?.value &&
            videoId == videoPlayerState.value.videoEntity?.id
        ) {
            updateVideoSource(
                videoId = videoId,
                updatedRelatedVideoList = true,
                autoplay = false
            )
        }
        if (result.hasLiveGate && videoPlayerState.value.videoEntity?.hasLiveGate == false) {
            videoPlayerState.value = videoPlayerState.value.copy(
                showPayWallControls = true,
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
        autoplay: Boolean
    ) {
        viewModelScope.launch(errorHandler) {
            val videoEntityDeferred = async { getVideoDetailsUseCase(videoId) }
            videoEntityDeferred.await()?.let { videoEntity ->
                videoPlayerState.value = videoPlayerState.value.copy(
                    videoEntity = videoEntity,
                    currentVote = getUserCurrentVote(videoEntity),
                    showPayWall = hasPremiumRestrictionUseCase(videoEntity)
                )
                videoPlayerState.value.rumblePlayer?.let { player ->
                    updateVideoPlayerSourceUseCase(
                        player = player,
                        videoEntity = videoEntity,
                        saveLastPosition = saveLastPositionUseCase::invoke,
                        screenId = videoDetailsScreen,
                        autoplay = autoplay,
                        updatedRelatedVideoList = updatedRelatedVideoList,
                        requestLiveGateData = true,
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

    private fun onNextVideo(videoId: Long, channelId: String, autoplay: Boolean) {
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
            autoplay = autoplay
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

    private suspend fun fetchChannelDetails(channelId: String): ChannelDetailsEntity? =
        viewModelScope.async { getChannelDataUseCase(channelId).getOrNull() }.await()
}