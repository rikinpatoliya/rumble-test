package com.rumble.videoplayer.presentation

import android.view.KeyEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXGiant
import com.rumble.theme.paddingXXXXLarge
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.keepScreenOn
import com.rumble.videoplayer.domain.model.VoteData
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.RumbleVideo
import com.rumble.videoplayer.player.cast.CastManager
import com.rumble.videoplayer.player.config.AdPlaybackState
import com.rumble.videoplayer.player.config.PlayerPlaybackState
import com.rumble.videoplayer.player.config.PlayerTarget
import com.rumble.videoplayer.player.config.ReportType
import com.rumble.videoplayer.player.config.RumbleVideoMode
import com.rumble.videoplayer.presentation.internal.controlViews.CastControlView
import com.rumble.videoplayer.presentation.internal.controlViews.EmbeddedControlsView
import com.rumble.videoplayer.presentation.internal.controlViews.FullScreenLandscapeControlsView
import com.rumble.videoplayer.presentation.internal.controlViews.FullScreenPortraitControlsView
import com.rumble.videoplayer.presentation.internal.controlViews.MobileErrorView
import com.rumble.videoplayer.presentation.internal.controlViews.PlayNextView
import com.rumble.videoplayer.presentation.internal.controlViews.TvControlsView
import com.rumble.videoplayer.presentation.internal.controlViews.TvErrorView
import com.rumble.videoplayer.presentation.internal.defaults.controlsInactiveDelay
import com.rumble.videoplayer.presentation.internal.views.AdLoadingScreen
import com.rumble.videoplayer.presentation.internal.views.CountDownView
import com.rumble.videoplayer.presentation.internal.views.LoadingScreen
import com.rumble.videoplayer.presentation.internal.views.PreviewTagView
import com.rumble.videoplayer.presentation.internal.views.ReplayScreen
import com.rumble.videoplayer.presentation.utils.requestFocusSafely
import kotlinx.coroutines.delay

@Composable
@androidx.annotation.OptIn(UnstableApi::class)
fun RumbleVideoView(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer,
    aspectRatioMode: Int = AspectRatioFrameLayout.RESIZE_MODE_FIT,
    playerBackgroundColor: Color = brandedPlayerBackground,
    uiType: UiType = UiType.EMBEDDED,
    dismissControlsDelay: Long = controlsInactiveDelay,
    isFullScreen: Boolean = false,
    isCollapsingMiniPlayerInProgress: Boolean = false,
    liveChatDisabled: Boolean = false,
    userVote: VoteData? = null,
    onChangeFullscreenMode: (Boolean) -> Unit = {},
    onLiveChatClicked: () -> Unit = {},
    onSettings: () -> Unit = {},
    onBack: () -> Unit = {},
    onClick: () -> Unit = {},
    onReport: (ReportType) -> Unit = {},
    onLike: () -> Unit = {},
    onDislike: () -> Unit = {},
    onChannelDetails: () -> Unit = {},
    onAddToPlaylist: () -> Unit = {},
    playListVideoCardComposable: @Composable (video: RumbleVideo, isPlaying: Boolean, onFocused: () -> Unit, onSelection: () -> Unit) -> Unit = { _, _, _, _ -> },
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val exoPlayer = remember { rumblePlayer.getPlayerInstance() }
    val playbackState by rumblePlayer.playbackState
    val adPlaybackState by rumblePlayer.adPlaybackState
    var showControls by rememberSaveable { mutableStateOf(false) }
    val playerTarget by remember { rumblePlayer.playerTarget }
    val castManager = CastManager(context, rumblePlayer)
    val controlsEnabled by rumblePlayer.controlsEnabled
    var seekInProgress by remember { mutableStateOf(false) }
    var afterSeek by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val hasRelatedVideos by rumblePlayer.hasRelatedVideos
    val currentCountDownValue by rumblePlayer.currentCountDownValue
    val countdownType by rumblePlayer.countDownType
    val rumbleVideoMode by rumblePlayer.rumbleVideoMode
    val playerView = remember {
        PlayerView(context).apply {
            player = exoPlayer
            useController = false
            resizeMode = aspectRatioMode
            hideController()
            setFullscreenButtonClickListener {
                onChangeFullscreenMode(it)
            }
        }
    }

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            rumblePlayer.onViewResumed(false)
        } else if (event == Lifecycle.Event.ON_RESUME) {
            rumblePlayer.onViewResumed(true)
            showControls =
                uiType == UiType.TV || ((playbackState is PlayerPlaybackState.Paused || playbackState is PlayerPlaybackState.Finished) && uiType != UiType.DISCOVER && uiType != UiType.IN_LIST)
        }
    }

    LaunchedEffect(showControls, seekInProgress, afterSeek, playbackState) {
        afterSeek = false
        if (playbackState is PlayerPlaybackState.Finished && uiType != UiType.DISCOVER && uiType != UiType.IN_LIST && controlsEnabled) {
            showControls = true
        } else if (showControls) {
            delay(dismissControlsDelay)
            if ((playbackState is PlayerPlaybackState.Playing) and seekInProgress.not()) showControls =
                false
        } else if (uiType == UiType.TV && playerTarget == PlayerTarget.LOCAL) {
            focusRequester.requestFocusSafely()
        }

        val keepOn =
            ((playbackState is PlayerPlaybackState.Paused || playbackState is PlayerPlaybackState.Finished) && playerTarget != PlayerTarget.AD).not()
        context.keepScreenOn(keepOn)
    }

    LaunchedEffect(controlsEnabled) {
        if (uiType != UiType.DISCOVER && uiType != UiType.IN_LIST)
            showControls = showControls && controlsEnabled
    }

    SideEffect {
        rumblePlayer.updateUiType(uiType)
    }

    DisposableEffect(Unit) {
        context.keepScreenOn(true)
        castManager.startListen()
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            castManager.stopListen()
            context.keepScreenOn(false)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = modifier.background(playerBackgroundColor)
    ) {
        if (playerTarget == PlayerTarget.REMOTE) {
            CastControlView(
                modifier = Modifier.fillMaxSize(),
                rumblePlayer = rumblePlayer
            )
        } else if (playbackState !is PlayerPlaybackState.Idle) {
            if (playerTarget != PlayerTarget.AD || uiType == UiType.TV) {
                AndroidView(
                    modifier = Modifier
                        .focusable(enabled = true)
                        .conditional(uiType == UiType.TV) {
                            focusRequester(focusRequester)
                        }
                        .fillMaxSize()
                        .clickableNoRipple {
                            if (uiType != UiType.DISCOVER && uiType != UiType.IN_LIST && uiType != UiType.TV)
                                showControls = showControls.not()
                            onClick()
                        }
                        .onKeyEvent { event ->
                            if (event.nativeKeyEvent.keyCode != android.view.KeyEvent.KEYCODE_BACK) {
                                if (uiType == UiType.TV) handleKeyEvent(event, rumblePlayer)
                                if (event.nativeKeyEvent.action == ACTION_UP) showControls = true
                            } else {
                                focusManager.clearFocus(force = true)
                            }
                            false
                        },
                    factory = {
                        playerView
                    },
                    update = { playerView ->
                        if (isFullScreen)
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        else if (isCollapsingMiniPlayerInProgress)
                            playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                        else
                            playerView.resizeMode = aspectRatioMode
                    }
                )
            }

            LoadingScreen(
                modifier = modifier,
                thumbnail = rumblePlayer.videoThumbnailUri,
                uiType = uiType,
                isVisible = (playbackState is PlayerPlaybackState.Fetching)
                        || (playbackState is PlayerPlaybackState.Paused && uiType == UiType.IN_LIST) || playbackState.isBuffering
            )

            if (playbackState !is PlayerPlaybackState.Fetching && playerTarget != PlayerTarget.AD) {
                if (playbackState is PlayerPlaybackState.Finished && hasRelatedVideos) {
                    rumblePlayer.nextRelatedVideo?.let {
                        PlayNextView(
                            uiType = uiType,
                            rumbleVideo = it,
                            rumbleVideoMode = rumbleVideoMode,
                            delayInitialCount = rumblePlayer.playNextCurrentCount,
                            onPlayNextCountChanged = rumblePlayer::onPlayNextCountChanged,
                            onCancel = rumblePlayer::onCancelNextVideo,
                            onPlayNow = rumblePlayer::onPlayNextVideo
                        )
                    }
                } else if (playbackState is PlayerPlaybackState.Error) {
                    rumblePlayer.rumbleVideo?.let {
                        when (uiType) {
                            UiType.TV -> {
                                TvErrorView(
                                    modifier = Modifier.fillMaxSize(),
                                    rumbleVideo = it,
                                    onChannelDetailsClick = onChannelDetails
                                )
                            }

                            else -> {
                                MobileErrorView(
                                    modifier = Modifier.fillMaxSize(),
                                    rumbleVideo = it,
                                    uiType = uiType,
                                    onBack = onBack
                                )
                            }
                        }
                    }
                } else {
                    if (playbackState is PlayerPlaybackState.Finished) {
                        ReplayScreen(
                            modifier = Modifier.fillMaxSize(),
                            thumbnail = rumblePlayer.videoThumbnailUri
                        )
                    }

                    when (uiType) {
                        UiType.EMBEDDED -> {

                            if (seekInProgress.not()) {
                                PreviewTagView(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(paddingMedium),
                                    countDownValue = currentCountDownValue,
                                    type = countdownType,
                                )

                                CountDownView(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(paddingMedium),
                                    countDownValue = currentCountDownValue,
                                    type = countdownType,
                                    uiType = uiType
                                )
                            }

                            EmbeddedControlsView(
                                isVisible = showControls,
                                isFullScreen = isFullScreen,
                                rumblePlayer = rumblePlayer,
                                onChangeFullscreenMode = onChangeFullscreenMode,
                                onMore = onSettings,
                                onSeekInProgress = {
                                    showControls = it
                                    seekInProgress = it
                                },
                                onSeek = { afterSeek = true },
                                onBack = onBack
                            )
                        }

                        UiType.FULL_SCREEN_LANDSCAPE -> {

                            if (seekInProgress.not()) {
                                PreviewTagView(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(
                                            horizontal = paddingMedium,
                                            vertical = paddingXXXXLarge
                                        ),
                                    countDownValue = currentCountDownValue,
                                    type = countdownType,
                                )

                                CountDownView(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(
                                            horizontal = paddingMedium,
                                            vertical = paddingXXXXLarge
                                        ),
                                    countDownValue = currentCountDownValue,
                                    type = countdownType,
                                    uiType = uiType
                                )
                            }

                            FullScreenLandscapeControlsView(
                                isVisible = showControls,
                                isFullScreen = isFullScreen,
                                rumblePlayer = rumblePlayer,
                                onSeekInProgress = { seekInProgress = it },
                                onSeek = { afterSeek = true },
                                onChangeFullscreenMode = onChangeFullscreenMode,
                                onLiveChatClicked = onLiveChatClicked,
                                liveChatDisabled = liveChatDisabled,
                                onSettings = onSettings
                            )
                        }

                        UiType.TV -> {

                            PreviewTagView(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = paddingMedium, vertical = paddingXXGiant),
                                countDownValue = currentCountDownValue,
                                type = countdownType,
                            )

                            CountDownView(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(
                                        horizontal = paddingMedium,
                                        vertical = paddingXXXXLarge
                                    ),
                                countDownValue = currentCountDownValue,
                                type = countdownType,
                                uiType = uiType
                            )

                            TvControlsView(
                                rumblePlayer = rumblePlayer,
                                isVisible = showControls,
                                currentVote = userVote,
                                onActionInProgress = { seekInProgress = it },
                                onReport = onReport,
                                onLike = onLike,
                                onDislike = onDislike,
                                onAddToPlaylist = onAddToPlaylist,
                                onBack = { showControls = false },
                                onChannelDetailsClick = onChannelDetails,
                                videoCardComposable = playListVideoCardComposable
                            )
                        }

                        UiType.FULL_SCREEN_PORTRAIT -> {

                            if (seekInProgress.not()) {
                                PreviewTagView(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(paddingMedium),
                                    countDownValue = currentCountDownValue,
                                    type = countdownType,
                                )

                                CountDownView(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(paddingMedium),
                                    countDownValue = currentCountDownValue,
                                    type = countdownType,
                                    uiType = uiType
                                )
                            }

                            FullScreenPortraitControlsView(
                                isVisible = showControls,
                                isFullScreen = isFullScreen,
                                rumblePlayer = rumblePlayer,
                                onSeekInProgress = { seekInProgress = it },
                                onSeek = { afterSeek = true },
                                onChangeFullscreenMode = onChangeFullscreenMode,
                                onSettings = onSettings
                            )
                        }

                        else -> return
                    }
                }
            }

            if (playerTarget == PlayerTarget.AD) {
                if (adPlaybackState is AdPlaybackState.Buffering) {
                    AdLoadingScreen(modifier = Modifier.fillMaxSize())
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(playerBackgroundColor)
                    ) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = {
                                rumblePlayer.adPlayerView
                                    .apply { resizeMode = aspectRatioMode }
                                    .also {
                                        if (uiType != UiType.TV && it.parent != null) {
                                            (it.parent as? ViewGroup)?.removeView(it)
                                        }
                                    }
                            },
                            update = { playerView ->
                                if (isFullScreen)
                                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                else if (isCollapsingMiniPlayerInProgress)
                                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                                else
                                    playerView.resizeMode = aspectRatioMode

                                if (rumbleVideoMode == RumbleVideoMode.Normal || uiType == UiType.TV) {
                                    playerView.adViewGroup.visibility = View.VISIBLE
                                } else {
                                    playerView.adViewGroup.visibility = View.GONE
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun handleKeyEvent(event: KeyEvent, rumblePlayer: RumblePlayer) {
    val key = event.nativeKeyEvent.keyCode
    if (event.nativeKeyEvent.action == ACTION_UP) {
        when (key) {
            android.view.KeyEvent.KEYCODE_MEDIA_PLAY -> rumblePlayer.playVideo()
            android.view.KeyEvent.KEYCODE_MEDIA_PAUSE -> rumblePlayer.pauseVideo()
            android.view.KeyEvent.KEYCODE_MEDIA_REWIND -> if (rumblePlayer.enableSeekBar) rumblePlayer.seekBack()
            android.view.KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> if (rumblePlayer.enableSeekBar) rumblePlayer.seekForward()
            android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                if (rumblePlayer.isPlaying()) rumblePlayer.pauseVideo()
                else rumblePlayer.playVideo()
            }
        }
    }
}

