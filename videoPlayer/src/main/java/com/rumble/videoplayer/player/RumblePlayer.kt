package com.rumble.videoplayer.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.IBinder
import androidx.annotation.OptIn
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.ima.ImaAdsLoader
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import com.google.ads.interactivemedia.v3.api.Ad
import com.google.ads.interactivemedia.v3.api.AdEvent
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.rumble.analytics.AnalyticEvent
import com.rumble.analytics.ImaClickedEvent
import com.rumble.analytics.ImaCompletedEvent
import com.rumble.analytics.ImaDestroyedEvent
import com.rumble.analytics.ImaFailedEvent
import com.rumble.analytics.ImaImpressionEvent
import com.rumble.analytics.ImaImpressionNoAutoplayEvent
import com.rumble.analytics.ImaRequestedEvent
import com.rumble.analytics.ImaSkippedEvent
import com.rumble.analytics.ImaVideoStartedEvent
import com.rumble.analytics.MediaErrorData
import com.rumble.analytics.PRE_ROLL_FAILED
import com.rumble.network.connection.NetworkTypeResolver
import com.rumble.network.queryHelpers.PublisherId
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants.PLAYER_STATE_UPDATE_RATIO
import com.rumble.videoplayer.R
import com.rumble.videoplayer.domain.ads.PlayerAdsHelper
import com.rumble.videoplayer.domain.model.VideoAdDataEntity
import com.rumble.videoplayer.domain.usecases.GetCurrentDeviceVolumeUseCase
import com.rumble.videoplayer.domain.usecases.GetNextRelatedVideoUseCase
import com.rumble.videoplayer.domain.usecases.GetVideoSourceUseCase
import com.rumble.videoplayer.domain.usecases.HasNextRelatedVideoUseCase
import com.rumble.videoplayer.domain.usecases.ResetWatchedTimeSinceLastAdUseCase
import com.rumble.videoplayer.domain.usecases.UpdateWatchedTimeSinceLastAdUseCase
import com.rumble.videoplayer.player.config.AdPlaybackState
import com.rumble.videoplayer.player.config.BackgroundMode
import com.rumble.videoplayer.player.config.DefaultResolution
import com.rumble.videoplayer.player.config.LiveVideoReportResult
import com.rumble.videoplayer.player.config.PlaybackSpeed
import com.rumble.videoplayer.player.config.PlayerPlaybackState
import com.rumble.videoplayer.player.config.PlayerTarget
import com.rumble.videoplayer.player.config.PlayerVideoSource
import com.rumble.videoplayer.player.config.StreamStatus
import com.rumble.videoplayer.player.internal.notification.PlayListType
import com.rumble.videoplayer.player.internal.notification.RumblePlayList
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.adCheckDelta
import com.rumble.videoplayer.presentation.internal.defaults.adCountDownDelay
import com.rumble.videoplayer.presentation.internal.defaults.adPauseDelay
import com.rumble.videoplayer.presentation.internal.defaults.liveVideoSeekBuffer
import com.rumble.videoplayer.presentation.internal.defaults.maxBufferSize
import com.rumble.videoplayer.presentation.internal.defaults.maxCountDown
import com.rumble.videoplayer.presentation.internal.defaults.mintBufferSize
import com.rumble.videoplayer.presentation.internal.defaults.seekDuration
import com.rumble.videoplayer.presentation.internal.defaults.watchProgressDelay
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


@EntryPoint
@InstallIn(SingletonComponent::class)
interface PlayerEntryPoint {
    fun getVideoSourceUseCase(): GetVideoSourceUseCase
    fun networkTypeResolver(): NetworkTypeResolver
    fun sessionManager(): SessionManager
    fun updateWatchedTimeUseCase(): UpdateWatchedTimeSinceLastAdUseCase
    fun resetWatchedTimeUseCase(): ResetWatchedTimeSinceLastAdUseCase
    fun hasNextRelatedVideoUseCase(): HasNextRelatedVideoUseCase
    fun getNextRelatedVideoUseCase(): GetNextRelatedVideoUseCase
    fun getCurrentDeviceVolumeUseCase(): GetCurrentDeviceVolumeUseCase
}

private const val TAG = "RumblePlayer"

@OptIn(UnstableApi::class)
class RumblePlayer(
    private val viewerId: String,
    private val applicationContext: Context,
    private var defaultVideoResolution: Int? = null,
    private var defaultBitrate: Int? = null,
    private val onVideoQualityChanged: (PlayerVideoSource, Boolean) -> Unit = { _, _ -> },
    private val livePingInterval: Long,
    private val watchedTimeInterval: Long,
    private val useAutoQualityForLiveVideo: Boolean,
    private val getAutoplayValue: () -> Boolean,
    private val sendAnalyticsEvent: (AnalyticEvent, Boolean) -> Unit,
    private val sendMediaError: (MediaErrorData) -> Unit,
    private val sendError: (String, Throwable) -> Unit
) {
    // Players
    private var player: ExoPlayer
    private var adsPlayer: ExoPlayer? = null

    // Jobs
    private var countDownJob: Job = Job()
    private var progressJob: Job = Job()
    private var reportLiveVideoJob: Job = Job()
    private var timeRangeJob: Job = Job()
    private val supervisorJob = SupervisorJob()
    private val backgroundScope = CoroutineScope(Dispatchers.IO + supervisorJob)
    private val errorHandler: CoroutineExceptionHandler

    // External callbacks
    private var reportLiveVideo: (suspend (Long, String) -> LiveVideoReportResult?)? = null
    private var fetchPreRollData: (suspend (Long, Float, Long, PublisherId, Boolean) -> VideoAdDataEntity)? =
        null
    private var onLiveVideoReport: ((Long, Long, Int?) -> Unit)? = null
    private var saveLastPosition: ((Long, Long) -> Unit)? = null
    private var onNextVideo: ((Long, String, Boolean) -> Unit)? = null
    private var onTrackWatchedTime: (suspend () -> Unit)? = null
    private var onTimeRange: ((TimeRangeData) -> Unit)? = null
    private var onVideoSizeDefined: ((Int, Int) -> Unit)? = null
    private var reportAdEvent: (suspend (List<String>, Long) -> Unit)? = null
    private var sendInitialPlaybackEvent: (() -> Unit)? = null

    // Internal logic
    private val getCurrentDeviceVolumeUseCase: GetCurrentDeviceVolumeUseCase
    private val getNextRelatedVideoUseCase: GetNextRelatedVideoUseCase
    private val hasNextRelatedVideoUseCase: HasNextRelatedVideoUseCase
    private val updateWatchedTimeSinceLastAdUseCase: UpdateWatchedTimeSinceLastAdUseCase
    private val resetWatchedTimeSinceLastAdUseCase: ResetWatchedTimeSinceLastAdUseCase
    private val getVideoSourceUseCase: GetVideoSourceUseCase
    private val serviceConnection: ServiceConnection
    private val networkTypeResolver: NetworkTypeResolver
    private val sessionManager: SessionManager
    private var binder: RumblePlayerService.PlayerBinder? = null
    private val playerAdsHelper = PlayerAdsHelper()

    // Private fields
    private var currentPlaybackSpeed: PlaybackSpeed = PlaybackSpeed.NORMAL
    private var currentVideoSource: PlayerVideoSource? = null
    private var firstStart: Boolean = true
    private var resumeAfterSeek = false
    private var watchedTimeLeftOver: Long = 0
    private var watchedTime: Long = 0
    private var lastReportedWatchedTime: Long = 0
    private var startWatchTime: Long = 0
    private var initialTrackWatchedTimeReport = true
    private var playerStartPosition: Long = 0
    private var systemStartTime: Long = 0
    private var timeRangeUpdateInterval = watchProgressDelay
    private var remoteIsPlaying: Boolean = false
    private var lastTimeRange = TimeRangeData()
    private var initTime: Long = System.currentTimeMillis()
    private var startEventReported = false
    private var preRollData: VideoAdDataEntity = VideoAdDataEntity()
    private var autoPlay = false
    private var adsLoader: ImaAdsLoader? = null
    private var viewResumed: Boolean = false
    private var lastResume: Long = System.currentTimeMillis()
    private var playListIdList: List<Long> = emptyList()
    private var uiType: UiType = UiType.IN_LIST

    private var _adPlaybackState: MutableState<AdPlaybackState> =
        mutableStateOf(AdPlaybackState.None)
    internal val adPlaybackState: State<AdPlaybackState> = _adPlaybackState

    private var _playbackSate: MutableState<PlayerPlaybackState> =
        mutableStateOf(PlayerPlaybackState.Idle())
    internal val playbackState: State<PlayerPlaybackState> = _playbackSate

    private var _progressPercentage: MutableState<Float> = mutableFloatStateOf(0f)
    internal val progressPercentage: State<Float> = _progressPercentage

    private var _buggeredPercentage: MutableState<Float> = mutableFloatStateOf(0f)
    internal val buggeredPercentage: State<Float> = _buggeredPercentage

    private var _hasRelatedVideos: MutableState<Boolean> = mutableStateOf(false)
    internal val hasRelatedVideos: State<Boolean> = _hasRelatedVideos

    private var _totalTime: MutableState<Float> = mutableFloatStateOf(0f)
    val totalTime: State<Float> = _totalTime

    private var _currentPosition: MutableState<Float> = mutableFloatStateOf(0f)
    val currentPosition: State<Float> = _currentPosition

    private var _playerTarget: MutableState<PlayerTarget> = mutableStateOf(PlayerTarget.LOCAL)
    val playerTarget: State<PlayerTarget> = _playerTarget

    private var _controlsEnabled: MutableState<Boolean> = mutableStateOf(true)
    val controlsEnabled: State<Boolean> = _controlsEnabled

    private var _isMuted: MutableState<Boolean> = mutableStateOf(false)
    val isMuted: State<Boolean> = _isMuted

    private var _currentCountDownValue: MutableState<Int> = mutableIntStateOf(0)
    val currentCountDownValue: State<Int> = _currentCountDownValue

    internal var rumbleVideo: RumbleVideo? = null
    internal var autoPlayEnabled: Boolean = false
    internal var castLastPosition = 0L
    internal var remoteMediaClient: RemoteMediaClient? = null
    internal var adPlayerView: PlayerView = PlayerView(applicationContext)
        .apply {
            useController = false
            hideController()
        }
    internal var relatedVideoList: List<RumbleVideo> = emptyList()
    internal var playList: RumblePlayList? = null
    internal var lastFocusedPlayListIndex = 0

    var targetChangeListener: PlayerTargetChangeListener? = null

    var enableMidRolls: Boolean = true

    val videoTitle: String
        get() = rumbleVideo?.title ?: ""

    val videoDescription: String
        get() = rumbleVideo?.description ?: ""

    val videoThumbnailUri: String
        get() = rumbleVideo?.videoThumbnailUri ?: ""

    val videoUrl: String?
        get() = currentVideoSource?.videoUrl

    val videoId: Long
        get() = rumbleVideo?.videoId ?: -1

    val streamStatus: StreamStatus
        get() = rumbleVideo?.streamStatus ?: StreamStatus.NotStream

    val supportsDvr: Boolean
        get() = rumbleVideo?.supportsDvr ?: false

    val enableSeekBar: Boolean
        get() = (streamStatus == StreamStatus.NotStream ||
            (streamStatus == StreamStatus.LiveStream && supportsDvr)) && controlsEnabled.value

    var watchingNow: Long = 0
        private set

    val currentPositionValue: Long
        get() = player.currentPosition

    val currentVideoAgeRestricted: Boolean
        get() = rumbleVideo?.ageRestricted ?: false

    val videoFinished: Boolean
        get() = _playbackSate.value == PlayerPlaybackState.Finished()

    var onVideoFinished: (() -> Unit)? = null

    val isLiveVideo: Boolean
        get() = streamStatus == StreamStatus.LiveStream || streamStatus == StreamStatus.OfflineStream

    val nextRelatedVideo: RumbleVideo?
        get() = getNextRelatedVideoUseCase(relatedVideoList, rumbleVideo)

    init {
        player = createPlayer(applicationContext)
        getCurrentDeviceVolumeUseCase = EntryPoints
            .get(applicationContext, PlayerEntryPoint::class.java)
            .getCurrentDeviceVolumeUseCase()
        getNextRelatedVideoUseCase = EntryPoints
            .get(applicationContext, PlayerEntryPoint::class.java)
            .getNextRelatedVideoUseCase()
        hasNextRelatedVideoUseCase = EntryPoints
            .get(applicationContext, PlayerEntryPoint::class.java)
            .hasNextRelatedVideoUseCase()
        updateWatchedTimeSinceLastAdUseCase = EntryPoints
            .get(applicationContext, PlayerEntryPoint::class.java)
            .updateWatchedTimeUseCase()
        resetWatchedTimeSinceLastAdUseCase = EntryPoints
            .get(applicationContext, PlayerEntryPoint::class.java)
            .resetWatchedTimeUseCase()
        getVideoSourceUseCase = EntryPoints.get(applicationContext, PlayerEntryPoint::class.java)
            .getVideoSourceUseCase()
        networkTypeResolver = EntryPoints
            .get(applicationContext, PlayerEntryPoint::class.java).networkTypeResolver()
        sessionManager = EntryPoints
            .get(applicationContext, PlayerEntryPoint::class.java).sessionManager()
        errorHandler = CoroutineExceptionHandler { _, throwable ->
            sendError(TAG, throwable)
        }
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                binder = (service as? RumblePlayerService.PlayerBinder)
                setupService()
            }

            override fun onServiceDisconnected(arg0: ComponentName) {
            }
        }
    }

    internal fun getPlayerInstance(): ExoPlayer = player

    internal fun getAdsPlayerInstance(): ExoPlayer? = adsPlayer

    internal fun setPlaybackSpeed(speed: PlaybackSpeed) {
        if (speed != currentPlaybackSpeed) {
            if (isPlaying()) {
                notifyTimeRange()
                setTimeRangeStartPosition()
            }
            currentPlaybackSpeed = speed
            player.setPlaybackSpeed(speed.value)
        }
    }

    internal fun getCurrentSpeed() = currentPlaybackSpeed

    internal fun getSourceList(): List<PlayerVideoSource> {
        return rumbleVideo?.videoList ?: emptyList()
    }

    internal fun getCurrentVideoSource() = currentVideoSource

    internal fun setCurrentVideoSource(videoSource: PlayerVideoSource) {
        currentVideoSource = videoSource
        currentVideoSource?.let {
            defaultVideoResolution = it.resolution
            onVideoQualityChanged(
                it,
                rumbleVideo?.streamStatus == StreamStatus.LiveStream || rumbleVideo?.streamStatus == StreamStatus.OfflineStream
            )
        }
        if (playbackState.value !is PlayerPlaybackState.Finished) {
            notifyTimeRange()
            setTimeRangeStartPosition()
            val cachedPosition = player.currentPosition
            player.setMediaItem(buildMediaItem(videoSource.videoUrl))
            _playbackSate.value = PlayerPlaybackState.Playing(false)
            player.prepare()
            player.playWhenReady = true
            player.seekTo(cachedPosition)
        }
    }

    internal fun setPlayerTarget(target: PlayerTarget) {
        _playerTarget.value = target
        targetChangeListener?.onPlayerTargetChanged(target)
    }

    internal fun onSeekChanged(inProgress: Boolean) {
        if (inProgress and isPlaying()) {
            pauseVideo()
            resumeAfterSeek = true
        } else if (inProgress.not() and resumeAfterSeek) {
            _playbackSate.value = PlayerPlaybackState.Playing(false)
            playVideo()
            resumeAfterSeek = false
            handleAdAfterSeek()
        }
    }

    internal fun onCancelNextVideo() {
        _hasRelatedVideos.value = false
    }

    internal fun onPlayNextVideo() {
        getNextRelatedVideoUseCase(relatedVideoList, rumbleVideo)?.let {
            playNextVideo(it, true)
        }
    }

    internal fun onRemoteTargetPlaying(remoteIsPlaying: Boolean) {
        if (remoteIsPlaying) {
            setTimeRangeStartPosition()
            trackTimeRange()
        } else if (isPlaying()) {
            notifyTimeRange()
            timeRangeJob.cancel()
        }
        this.remoteIsPlaying = remoteIsPlaying
    }

    internal fun onViewResumed(resumed: Boolean) {
        viewResumed = resumed
        if (viewResumed) {
            setupService()
            startReport()
        }
    }

    internal fun updateUiType(type: UiType) {
        uiType = type
        handleTimeRang()
    }

    fun setVideo(
        video: RumbleVideo,
        playList: RumblePlayList?,
        reportLiveVideo: (suspend (Long, String) -> LiveVideoReportResult?)?,
        onLiveVideoReport: ((Long, Long, Int?) -> Unit)?,
        saveLastPosition: ((Long, Long) -> Unit)?,
        onVideoSizeDefined: ((Int, Int) -> Unit)?,
        onTrackWatchedTime: (suspend () -> Unit)?,
        onTimeRange: ((TimeRangeData) -> Unit)?,
        onNextVideo: ((Long, String, Boolean) -> Unit)?,
        fetchPreRollList: (suspend (Long, Float, Long, PublisherId, Boolean) -> VideoAdDataEntity)?,
        reportAdEvent: (suspend (List<String>, Long) -> Unit)?,
        sendInitialPlaybackEvent: (() -> Unit)?
    ) {
        initTime = System.currentTimeMillis()
        this.reportLiveVideo = reportLiveVideo
        this.onLiveVideoReport = onLiveVideoReport
        this.onTrackWatchedTime = onTrackWatchedTime
        this.onTimeRange = onTimeRange
        this.onVideoSizeDefined = onVideoSizeDefined
        this.saveLastPosition = saveLastPosition
        this.onNextVideo = onNextVideo
        this.fetchPreRollData = fetchPreRollList
        this.reportAdEvent = reportAdEvent
        this.sendInitialPlaybackEvent = sendInitialPlaybackEvent
        relatedVideoList = video.relatedVideoList
        _hasRelatedVideos.value =
            hasNextRelatedVideoUseCase(video.relatedVideoList, video, getAutoplayValue())
        autoPlayEnabled = relatedVideoList.isNotEmpty()
        resetState()
        initVideoData(video)
        playList?.let {
            updatePlayList(it)
        } ?: run {
            initUpNextList()
        }
        initPreRollData()
        listenToProgress(player)
        connectToService()
        setupService()
        startReport()
    }

    fun updateCurrentVideo(
        video: RumbleVideo,
        onSaveLastPosition: ((Long, Long) -> Unit)?,
        updatedRelatedVideoList: Boolean,
        autoPlay: Boolean
    ) {
        this.autoPlay = autoPlay
        initTime = System.currentTimeMillis()
        saveLastPosition = onSaveLastPosition
        if (updatedRelatedVideoList) {
            relatedVideoList = video.relatedVideoList
            _hasRelatedVideos.value =
                hasNextRelatedVideoUseCase(video.relatedVideoList, video, getAutoplayValue())
            autoPlayEnabled = relatedVideoList.isNotEmpty()
            initUpNextList()
        }
        resetState()
        initVideoData(video)
        initPreRollData()
        listenToProgress(player)
        connectToService()
        setupService()
        startReport()
        initFocusIndex()
    }

    fun playVideo() {
        if (playerTarget.value == PlayerTarget.LOCAL) {
            player.prepare()
            player.playWhenReady = true
        }
        trackTimeRange()
        startTrackWatchedTime()
    }

    fun pauseVideo() {
        if (isPlaying()) {
            notifyTimeRange()
            setTimeRangeStartPosition()
        } else if (playerTarget.value == PlayerTarget.AD) {
            pauseAdPlayer()
        }
        _playbackSate.value =
            if (player.currentPosition != 0L && player.currentPosition >= player.duration && isLiveVideo.not() && player.duration > 0) {
                onVideoFinished?.invoke()
                PlayerPlaybackState.Finished()
            } else PlayerPlaybackState.Paused(false)
        player.pause()
        timeRangeJob.cancel()
        saveLastPosition()
        stopTrackingWatchedTime()
    }

    fun seekBack(duration: Int = seekDuration) {
        if (isPlaying()) notifyTimeRange()
        var seekTo = player.contentPosition - duration
        if (seekTo < 0) seekTo = 0
        player.seekTo(seekTo)
        setTimeRangeStartPosition()
    }

    fun seekForward(duration: Int = seekDuration) {
        if (isFinished().not()) {
            if (isPlaying()) notifyTimeRange()
            var seekTo = player.currentPosition + duration
            if (seekTo >= player.duration) {
                seekTo = player.duration
                player.play()
            }
            player.seekTo(seekTo)
            setTimeRangeStartPosition()
        }
        handleAdAfterSeek()
    }

    fun seekTo(position: Long) {
        if (isPlaying()) notifyTimeRange()
        player.seekTo(position)
        setTimeRangeStartPosition()
    }

    fun seekToPercentage(percentage: Float) {
        if (isPlaying()) notifyTimeRange()
        var position = (player.duration * percentage).toLong()
        if (position >= player.duration) {
            position = player.duration
        }
        player.seekTo(position)
        setTimeRangeStartPosition()
    }

    fun mute() {
        player.volume = 0f
        _isMuted.value = true
    }

    fun unMute() {
        player.volume = 1f
        _isMuted.value = false
    }

    fun stopPlayer() {
        if (_playerTarget.value == PlayerTarget.AD) {
            sendAnalyticsEvent(ImaDestroyedEvent, true)
        }
        if (isPlaying()) notifyTimeRange()
        saveLastPosition()
        releaseAdPlayer()
        adsLoader?.release()
        playerAdsHelper.onClear()
        _playbackSate.value = PlayerPlaybackState.PlayerPlaybackReleased()
        progressJob.cancel()
        reportLiveVideoJob.cancel()
        timeRangeJob.cancel()
        countDownJob.cancel()
        supervisorJob.cancel()
        player.stop()
        player.release()
        binder?.stopPlay()
        binder?.let {
            applicationContext.unbindService(serviceConnection)
            binder = null
        }
    }

    fun replay() {
        _playbackSate.value = PlayerPlaybackState.Playing(false)
        _hasRelatedVideos.value =
            hasNextRelatedVideoUseCase(relatedVideoList, rumbleVideo, getAutoplayValue())
        player.setMediaItem(buildMediaItem(currentVideoSource?.videoUrl))
        player.prepare()
        player.playWhenReady = true
        player.seekTo(0)
        trackTimeRange()
    }

    fun isPlaying() =
        (_playbackSate.value is PlayerPlaybackState.Playing) || (_playerTarget.value == PlayerTarget.REMOTE && remoteIsPlaying)

    fun isPaused() = _playbackSate.value is PlayerPlaybackState.Paused

    fun isFetching() = _playbackSate.value is PlayerPlaybackState.Fetching

    fun isFinished() = _playbackSate.value is PlayerPlaybackState.Finished

    fun hideControls() {
        _controlsEnabled.value = false
    }

    fun enableControls() {
        _controlsEnabled.value = true
    }

    fun onPlayFromPlayList(videoId: Long) {
        playList?.videoList?.find { it.videoId == videoId }?.let {
            saveLastPosition()
            resetState()
            playNextVideo(it, false)
            initFocusIndex()
        }
    }

    fun updatePlayList(playList: RumblePlayList) {
        this.playList = playList
        playListIdList = playList.videoList.map { it.videoId }
        if (playList.shuffle) playListIdList = playListIdList.shuffled()
        initFocusIndex()
    }

    fun shufflePlayList(shuffle: Boolean) {
        playList?.let { playList ->
            playListIdList = if (shuffle) playList.videoList.map { it.videoId }.shuffled()
            else playList.videoList.map { it.videoId }
            rumbleVideo = playList.videoList.find { it.videoId == playListIdList.first() }
            initFocusIndex()
        }
    }

    fun loopPlayList(loop: Boolean) {
        playList?.let {
            playList = it.copy(loopPlayList = loop)
        }
    }

    private fun initFocusIndex() {
        val currentIndex = playList?.videoList?.indexOfFirst { it.videoId == rumbleVideo?.videoId }
            ?: 0
        lastFocusedPlayListIndex = if (currentIndex == (playList?.videoList?.size ?: 0) - 1) 0
        else currentIndex + 1
    }

    private fun playNextFromPlayList(): PlayerPlaybackState {
        saveLastPosition()
        val index = playListIdList.indexOf(rumbleVideo?.videoId)
        return if (playList?.loopPlayList == true || index < (playListIdList.size) - 1) {
            val nextIndex = if (index == (playListIdList.size) - 1) 0 else index + 1
            playList?.videoList?.find { it.videoId == playListIdList[nextIndex] }?.let {
                resetState()
                playNextVideo(it, false)
                initFocusIndex()
            }
            PlayerPlaybackState.Idle()
        } else {
            PlayerPlaybackState.Finished()
        }
    }

    private fun playNextVideo(nextVideo: RumbleVideo, autoPlay: Boolean) {
        _playbackSate.value = PlayerPlaybackState.Fetching()
        initialTrackWatchedTimeReport = true
        onNextVideo?.invoke(nextVideo.videoId, nextVideo.channelId, autoPlay)
    }

    private fun resetState() {
        setPlayerTarget(PlayerTarget.LOCAL)
        _playbackSate.value = PlayerPlaybackState.Idle()
        _adPlaybackState.value = AdPlaybackState.None
        playerAdsHelper.onClear()
        initialTrackWatchedTimeReport = true
        startEventReported = false
        _currentCountDownValue.value = 0
    }

    private fun initPreRollData() {
        rumbleVideo?.let {
            backgroundScope.launch {
                preRollData = fetchPreRollData?.invoke(
                    it.videoId,
                    sessionManager.watchedTimeSinceLastAd.first(),
                    initTime,
                    it.publisherId,
                    autoPlay
                ) ?: VideoAdDataEntity()
                playerAdsHelper.initPreRollList(
                    rumbleVideo = it,
                    preRollData = preRollData
                )
            }
        }
    }

    private fun initVideoData(video: RumbleVideo) {
        rumbleVideo = video
        watchingNow = video.watchingNow
        currentVideoSource = getVideoSourceUseCase(
            sourceList = getSourceList(),
            resolution = defaultVideoResolution,
            bitrate = defaultBitrate,
            defaultResolution = DefaultResolution.MOBILE,
            useLowQuality = video.useLowQuality,
            useAutoQualityForLiveVideo = useAutoQualityForLiveVideo
        ) ?: getSourceList().first()
        currentVideoSource?.let {
            playerStartPosition = video.lastPosition
            player.setMediaItem(buildMediaItem(it.videoUrl))
            player.prepare()
            player.seekTo(video.lastPosition)
            player.addAnalyticsListener(object : AnalyticsListener {
                override fun onVideoSizeChanged(
                    eventTime: AnalyticsListener.EventTime,
                    videoSize: VideoSize
                ) {
                    onVideoSizeDefined?.invoke(videoSize.width, videoSize.height)
                }
            })
        }
    }

    private fun initUpNextList() {
        playList = if (relatedVideoList.isNotEmpty())
            RumblePlayList(
                title = applicationContext.getString(R.string.up_next),
                videoList = relatedVideoList,
                type = PlayListType.UpNext
            )
        else null
    }

    private fun initTimeRangeUpdateInterval() = runBlocking {
        val configInterval = sessionManager.watchProgressIntervalFlow.first()
        timeRangeUpdateInterval =
            if (configInterval > 0) TimeUnit.SECONDS.toMillis(configInterval.toLong())
            else watchProgressDelay
    }

    private fun connectToService() {
        applicationContext.bindService(
            Intent(applicationContext, RumblePlayerService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    private fun setupService() {
        binder?.setPlayer(
            player = this,
            enableSeekBar = enableSeekBar,
            playInBackground = rumbleVideo?.backgroundMode == BackgroundMode.On,
            id = rumbleVideo?.videoId ?: 0
        ) { position: Long, id: Long ->
            saveLastPosition?.invoke(position, id)
        }
    }

    private fun createPlayer(context: Context): ExoPlayer {
        val loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(
            maxBufferSize,
            maxBufferSize,
            mintBufferSize,
            mintBufferSize
        ).build()
        val trackSelector = DefaultTrackSelector(applicationContext).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        val exoPlayer = ExoPlayer.Builder(context)
            .setLoadControl(loadControl)
            .setTrackSelector(trackSelector)
            .setDeviceVolumeControlEnabled(true)
            .setRenderersFactory(DefaultRenderersFactory(context).setEnableDecoderFallback(true))
            .build()
            .apply {
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
            }
        listenToPlayerState(exoPlayer)
        return exoPlayer
    }

    private fun createAdsLoader(context: Context, isMidRoll: Boolean) {
        adsLoader = ImaAdsLoader.Builder(context)
            .setAdEventListener { event ->
                when (event.type) {
                    AdEvent.AdEventType.LOADED -> {
                        if (isMidRoll) startCountDown()
                        else playAd()
                    }

                    AdEvent.AdEventType.ALL_ADS_COMPLETED -> {
                        if (_adPlaybackState.value != AdPlaybackState.None) {
                            sendAnalyticsEvent(ImaCompletedEvent, true)
                        }
                        _adPlaybackState.value = AdPlaybackState.None
                        playerAdsHelper.onPreRollPlayed()
                        loadAd()
                        backgroundScope.launch { resetWatchedTimeSinceLastAdUseCase() }
                    }

                    AdEvent.AdEventType.STARTED -> {
                        playerAdsHelper.currentPreRollUrl?.let {
                            reportAdEvent(it.impressionUrlList)
                            rumbleVideo?.let { rumbleVideo ->
                                sendAnalyticsEvent(
                                    ImaImpressionEvent(
                                        rumbleVideo.userId,
                                        rumbleVideo.channelId
                                    ), true
                                )
                            }
                            if (autoPlay.not()) {
                                reportAdEvent(it.pgImpressionUrlList)
                                sendAnalyticsEvent(ImaImpressionNoAutoplayEvent, true)
                            }
                        }
                    }

                    AdEvent.AdEventType.CLICKED -> {
                        if (rumbleVideo?.publisherId == PublisherId.AndroidTv) {
                            if (adsPlayer?.isPlaying == true) adsPlayer?.pause()
                            else playAd()
                        } else {
                            playerAdsHelper.currentPreRollUrl?.let {
                                reportAdEvent(it.clickUrlList)
                                sendAnalyticsEvent(ImaClickedEvent, true)
                            }
                        }
                    }

                    AdEvent.AdEventType.SKIPPED -> {
                        _adPlaybackState.value = AdPlaybackState.None
                        sendAnalyticsEvent(ImaSkippedEvent, true)
                    }

                    AdEvent.AdEventType.PAUSED -> {
                        if (canPauseAd(event.ad)) {
                            _adPlaybackState.value = AdPlaybackState.Paused
                            adsPlayer?.pause()
                        }
                    }

                    AdEvent.AdEventType.RESUMED -> {
                        _adPlaybackState.value = AdPlaybackState.Resumed
                        adsPlayer?.playWhenReady = true
                        lastResume = System.currentTimeMillis()
                    }

                    else -> return@setAdEventListener
                }
            }
            .setAdErrorListener {
                setPlayerTarget(PlayerTarget.LOCAL)
                _adPlaybackState.value = AdPlaybackState.None
                sendErrorReport(it.error.message)
                sendAnalyticsEvent(ImaFailedEvent, true)
                sendError(PRE_ROLL_FAILED, it.error)
                loadAd()
            }
            .build()
    }

    private fun canPauseAd(ad: Ad) =
        ((System.currentTimeMillis() - lastResume) > adPauseDelay && adsPlayer?.isPlaying == true) &&
            (ad.isSkippable.not() || TimeUnit.MILLISECONDS.toSeconds(
                (adsPlayer?.currentPosition
                    ?: 0)
            ) > ad.skipTimeOffset)

    private fun createAdsPlayer(context: Context): ExoPlayer {
        val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(context)
        val mediaSourceFactory: MediaSource.Factory = DefaultMediaSourceFactory(dataSourceFactory)
            .setLocalAdInsertionComponents({ _ -> adsLoader }, adPlayerView)
        val exoPlayer = ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply { videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING }
        adsLoader?.setPlayer(exoPlayer)
        adPlayerView.player = exoPlayer
        listenToAdsPlayerState(exoPlayer)
        return exoPlayer
    }

    private fun listenToPlayerState(exoPlayer: ExoPlayer) {
        exoPlayer.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                _playbackSate.value = when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        when (this@RumblePlayer.playbackState.value) {
                            is PlayerPlaybackState.Idle -> PlayerPlaybackState.Fetching()
                            is PlayerPlaybackState.Playing -> PlayerPlaybackState.Playing(true)
                            is PlayerPlaybackState.Paused -> PlayerPlaybackState.Paused(true)
                            else -> _playbackSate.value
                        }
                    }

                    Player.STATE_ENDED -> {
                        onVideoFinished()
                        if (playList?.type == PlayListType.PlayList) {
                            onVideoFinished?.invoke()
                            playNextFromPlayList()
                        } else if (rumbleVideo?.loop == true) {
                            replay()
                            PlayerPlaybackState.Playing(false)
                        } else {
                            onVideoFinished?.invoke()
                            PlayerPlaybackState.Finished()
                        }
                    }

                    Player.STATE_READY -> {
                        val currentState = this@RumblePlayer.playbackState.value
                        if (currentState is PlayerPlaybackState.Fetching){
                            sendInitialPlaybackEvent?.invoke()
                        }
                        when (currentState) {
                            is PlayerPlaybackState.Paused -> PlayerPlaybackState.Paused(false)
                            is PlayerPlaybackState.Playing -> PlayerPlaybackState.Playing(false)
                            else -> _playbackSate.value
                        }
                    }

                    else -> return
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    checkForPreRoll()
                    setTimeRangeStartPosition()
                    _hasRelatedVideos.value = hasNextRelatedVideoUseCase(
                        relatedVideoList,
                        rumbleVideo,
                        getAutoplayValue()
                    )
                    if (startEventReported.not() && preRollData.preRollList.isNotEmpty()) {
                        reportAdEvent(preRollData.startUrlList)
                        sendAnalyticsEvent(ImaVideoStartedEvent, true)
                        startEventReported = true
                    }
                }
                if (isPlaying || isPlaying()) {
                    _playbackSate.value = PlayerPlaybackState.Playing(false)
                    handleFirstStart()
                } else if (player.currentPosition >= player.duration && player.duration > 0) {
                    onVideoFinished?.invoke()
                } else {
                    _playbackSate.value = PlayerPlaybackState.Paused(false)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _playbackSate.value = PlayerPlaybackState.Error()
                sendErrorReport(errorMessage = error.message ?: "Player PlaybackException")
                exoPlayer.prepare()
            }
        })
    }

    private fun listenToAdsPlayerState(exoPlayer: ExoPlayer) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                sendErrorReport(errorMessage = error.message ?: "Ads player PlaybackException")
                _adPlaybackState.value = AdPlaybackState.None
                sendAnalyticsEvent(ImaFailedEvent, true)
                loadAd()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) _adPlaybackState.value = AdPlaybackState.Resumed
            }
        })
    }

    private fun updateWatchedTime(exoPlayer: ExoPlayer) {
        if (exoPlayer.duration > 0) {
            if (isPlaying()) {
                if (startWatchTime == 0L) startWatchTime = System.currentTimeMillis()
                watchedTime += System.currentTimeMillis() - startWatchTime - watchedTime
                if ((watchedTime + watchedTimeLeftOver) - lastReportedWatchedTime > watchedTimeInterval / currentPlaybackSpeed.value) {
                    lastReportedWatchedTime = watchedTime
                    watchedTimeLeftOver = 0
                    trackWatchedTime()
                } else if (watchedTimeLeftOver > watchedTimeInterval / currentPlaybackSpeed.value) {
                    watchedTimeLeftOver = 0
                    trackWatchedTime()
                }
            }
        }
    }

    private fun handleFirstStart() {
        if (firstStart) {
            firstStart = false
            adjustLiveVideoPosition()
        }
    }

    private fun adjustLiveVideoPosition() {
        if (rumbleVideo?.streamStatus == StreamStatus.LiveStream && rumbleVideo?.supportsDvr == true) {
            val totalDuration = player.duration.coerceAtLeast(0L).toFloat()
            val seekTo = totalDuration - liveVideoSeekBuffer
            if (seekTo > 0) seekTo(seekTo.toLong())
        }
    }

    private fun buildMediaItem(videoUrl: String?): MediaItem {
        val mediaData = buildMediaData(rumbleVideo)
        return MediaItem.Builder()
            .setMediaMetadata(mediaData)
            .setUri(videoUrl)
            .build()
    }

    private fun buildMediaData(rumbleVideo: RumbleVideo?) =
        MediaMetadata.Builder()
            .setArtworkUri(Uri.parse(rumbleVideo?.videoThumbnailUri))
            .setTitle(rumbleVideo?.title)
            .setDisplayTitle(rumbleVideo?.title)
            .setDescription(rumbleVideo?.description)
            .setAlbumTitle(rumbleVideo?.title)
            .setSubtitle(rumbleVideo?.description)
            .setAlbumArtist(rumbleVideo?.title)
            .setArtist(rumbleVideo?.description)
            .build()

    private fun listenToProgress(exoPlayer: ExoPlayer) {
        progressJob = backgroundScope.launch {
            while (isActive) {
                withContext(Dispatchers.Main) { checkForPreRoll() }
                delay(PLAYER_STATE_UPDATE_RATIO)
                if (playerTarget.value == PlayerTarget.LOCAL) {
                    withContext(Dispatchers.Main) {
                        updateProgressValues(exoPlayer)
                        updateWatchedTime(exoPlayer)
                    }
                }
            }
        }
    }

    private fun checkForPreRoll() {
        if (playerTarget.value == PlayerTarget.LOCAL && uiType != UiType.IN_LIST) {
            if (_adPlaybackState.value !is AdPlaybackState.Buffering) {
                if (playerAdsHelper.hasPreRollForPosition(player.currentPosition, isLiveVideo)) {
                    loadAd(playerAdsHelper.currentIsMidRoll())
                }
            }
        }
    }

    private fun handleAdAfterSeek() {
        if (playerTarget.value == PlayerTarget.LOCAL && _adPlaybackState.value !is AdPlaybackState.Buffering) {
            if (player.currentPosition >= (player.duration - adCheckDelta)) {
                countDownJob.cancel()
                _currentCountDownValue.value = 0
            } else if (playerAdsHelper.hasPreRollAfterSeek(player.currentPosition)) {
                loadAd(playerAdsHelper.currentIsMidRoll())
            }
        }
    }

    private fun startCountDown() {
        if (_currentCountDownValue.value == 0) {
            _currentCountDownValue.value = maxCountDown
            countDownJob.cancel()
            countDownJob = backgroundScope.launch {
                while (isActive) {
                    delay(adCountDownDelay)
                    withContext(Dispatchers.Main) {
                        if (player.isPlaying) _currentCountDownValue.value -= 1
                        if (_currentCountDownValue.value == 1) {
                            delay(adCountDownDelay)
                            playAd()
                            _currentCountDownValue.value = 0
                            countDownJob.cancel()
                        }
                    }
                }
            }
        }
    }

    private fun loadAd(isMidRoll: Boolean = false) {
        if (_adPlaybackState.value !is AdPlaybackState.Buffering
            && (isMidRoll.not() || (viewResumed && enableMidRolls))
        ) {
            _adPlaybackState.value = AdPlaybackState.Buffering
            adsPlayer?.stop()
            playerAdsHelper.getNextPreRollUrl()?.let {
                try {
                    reportAdEvent(it.requestedUrlList)
                    rumbleVideo?.let { video ->
                        sendAnalyticsEvent(
                            ImaRequestedEvent(video.userId, video.channelId),
                            true
                        )
                    }
                    createAdsLoader(applicationContext, isMidRoll)
                    prepareAdPlayer(Uri.parse(it.url))
                } catch (e: Exception) {
                    sendError(TAG, e)
                    resumeVideo()
                }

            } ?: run {
                _adPlaybackState.value = AdPlaybackState.None
                resumeVideo()
            }
        }
    }

    private fun prepareAdPlayer(uri: Uri) {
        _adPlaybackState.value = AdPlaybackState.Buffering
        val mediaItem = MediaItem.Builder()
            .setUri(currentVideoSource?.videoUrl)
            .setAdsConfiguration(MediaItem.AdsConfiguration.Builder(uri).build())
            .build()
        adsPlayer = createAdsPlayer(applicationContext)
        adsPlayer?.setMediaItem(mediaItem)
        adsPlayer?.prepare()
    }

    private fun playAd() {
        if (_playerTarget.value == PlayerTarget.LOCAL) pauseVideo()
        setPlayerTarget(PlayerTarget.AD)
        _adPlaybackState.value = AdPlaybackState.Resumed
        adsPlayer?.play()
    }

    private fun resumeVideo() {
        _playbackSate.value = PlayerPlaybackState.Playing(false)
        setPlayerTarget(PlayerTarget.LOCAL)
        playVideo()
    }

    private fun startReport() {
        reportLiveVideoJob.cancel()
        if (isLiveVideo) {
            reportLiveVideoJob = backgroundScope.launch(errorHandler) {
                while (isActive) {
                    rumbleVideo?.videoId?.let { videoId ->
                        withContext(Dispatchers.Main) {
                            if (viewResumed || player.isPlaying) {
                                withContext(Dispatchers.IO) {
                                    reportLiveVideo?.invoke(videoId, viewerId)
                                        ?.let { reportResult ->
                                            onLiveVideoReport?.invoke(
                                                videoId,
                                                reportResult.watchingNow,
                                                reportResult.statusCode
                                            )
                                            if (reportResult.isLive.not()) reportLiveVideoJob.cancel()
                                            else watchingNow = reportResult.watchingNow
                                        }
                                }
                            }
                        }
                    }
                    delay(livePingInterval)
                }
            }
        }
    }

    private fun startTrackWatchedTime() {
        stopTrackingWatchedTime()
        if (initialTrackWatchedTimeReport) {
            trackWatchedTime()
            initialTrackWatchedTimeReport = false
        }
    }

    private fun stopTrackingWatchedTime() {
        watchedTimeLeftOver += if (lastReportedWatchedTime > 0) watchedTime - lastReportedWatchedTime else watchedTime
        watchedTime = 0
        startWatchTime = 0
        lastReportedWatchedTime = 0
    }

    private fun updateProgressValues(exoPlayer: ExoPlayer) {
        if (exoPlayer.duration > 0) {
            val totalDuration = player.duration.coerceAtLeast(0L).toFloat()
            val currentTime = player.currentPosition.coerceAtLeast(0L).toFloat()
            val percentage = player.bufferedPercentage.toFloat()
            if (resumeAfterSeek.not()) _totalTime.value = totalDuration
            _progressPercentage.value = currentTime / totalDuration
            _currentPosition.value = currentTime
            _buggeredPercentage.value = percentage
        } else {
            _progressPercentage.value = 0f
            _currentPosition.value = 0f
            _buggeredPercentage.value = 0f
        }
    }

    private fun saveLastPosition() {
        val lastPosition =
            if (player.currentPosition < player.duration) player.currentPosition else 0
        rumbleVideo?.videoId?.let { videoId ->
            saveLastPosition?.invoke(lastPosition, videoId)
        }
        playList?.videoList?.map {
            if (it.videoId == rumbleVideo?.videoId) it.copy(lastPosition = lastPosition)
            else it
        }?.let {
            playList = playList?.copy(videoList = it)
        }
    }

    private fun trackTimeRange() {
        timeRangeJob.cancel()
        initTimeRangeUpdateInterval()
        setTimeRangeStartPosition()

        timeRangeJob = backgroundScope.launch(errorHandler) {
            while (isActive) {
                delay(timeRangeUpdateInterval)
                withContext(Dispatchers.Main) {
                    handleTimeRang()
                }
            }
        }
    }

    private fun handleTimeRang() {
        if (player.isPlaying || isPlaying()) {
            notifyTimeRange()
            setTimeRangeStartPosition()
        }
    }

    private fun notifyTimeRange() {
        val startTime = playerStartPosition.toFloat() / 1000
        val duration = if (isLiveVideo || playerTarget.value == PlayerTarget.REMOTE) {
            ((System.currentTimeMillis() - systemStartTime).toFloat() / 1000) * currentPlaybackSpeed.value
        } else {
            player.currentPosition.toFloat() / 1000 - startTime
        }

        val timeRangeData = TimeRangeData(
            videoId = videoId,
            startTime = if (isLiveVideo) null else startTime,
            duration = duration,
            isPlaceholder = rumbleVideo?.streamStatus == StreamStatus.OfflineStream,
            playbackRate = if (currentPlaybackSpeed.value == 1f) null else currentPlaybackSpeed.value,
            playbackVolume = getCurrentDeviceVolumeUseCase(),
            muted = _isMuted.value,
            uiType = uiType
        )

        if (duration > 0 && timeRangeData != lastTimeRange) {
            lastTimeRange = timeRangeData
            onTimeRange?.invoke(timeRangeData)
            backgroundScope.launch { updateWatchedTimeSinceLastAdUseCase(duration) }
        }
    }

    private fun onVideoFinished() {
        notifyTimeRange()
        timeRangeJob.cancel()
    }

    private fun setTimeRangeStartPosition() {
        playerStartPosition = if (playerTarget.value == PlayerTarget.REMOTE) {
            remoteMediaClient?.approximateStreamPosition ?: 0
        } else {
            player.currentPosition
        }
        systemStartTime = System.currentTimeMillis()
    }

    private fun reportAdEvent(urlList: List<String>) {
        backgroundScope.launch {
            reportAdEvent?.invoke(urlList, initTime)
        }
    }

    private fun pauseAdPlayer() {
        adPlayerView.onPause()
        releaseAdPlayer()
    }

    private fun releaseAdPlayer() {
        adsLoader?.setPlayer(null)
        adPlayerView.player = null
        adsPlayer?.release()
        adsPlayer = null
    }

    private fun sendErrorReport(errorMessage: String) {
        val mediaErrorData = MediaErrorData(
            errorMessage = errorMessage,
            videoId = rumbleVideo?.videoId ?: 0L,
            videoUrl = currentVideoSource?.videoUrl ?: "",
            screenId = rumbleVideo?.screenId ?: "",
            backgroundMode = rumbleVideo?.backgroundMode?.toString()
                ?: BackgroundMode.On.toString(),
            playbackTime = player.currentPosition,
            volume = player.volume,
            playbackSpeed = currentPlaybackSpeed.value,
            quality = currentVideoSource?.qualityText ?: "",
            bitrate = currentVideoSource?.bitrateText ?: "",
            target = playerTarget.value.toString()
        )
        sendMediaError(mediaErrorData)
    }

    private fun trackWatchedTime() {
        backgroundScope.launch(errorHandler) {
            onTrackWatchedTime?.invoke()
        }
    }
}