package com.rumble.videoplayer.presentation.internal.controlViews

import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_BACK
import android.view.KeyEvent.KEYCODE_DPAD_CENTER
import android.view.KeyEvent.KEYCODE_DPAD_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_LEFT
import android.view.KeyEvent.KEYCODE_DPAD_RIGHT
import android.view.KeyEvent.KEYCODE_DPAD_UP
import android.view.KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
import android.view.KeyEvent.KEYCODE_MEDIA_PAUSE
import android.view.KeyEvent.KEYCODE_MEDIA_PLAY
import android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
import android.view.KeyEvent.KEYCODE_MEDIA_REWIND
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.theme.RumbleTypography
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingGiant
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXGiant
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.R
import com.rumble.videoplayer.domain.model.VoteData
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.RumbleVideo
import com.rumble.videoplayer.player.config.PlayerPlaybackState
import com.rumble.videoplayer.player.config.ReportType
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.controlsInactiveDelay
import com.rumble.videoplayer.presentation.internal.defaults.gradientBottomGuideLine
import com.rumble.videoplayer.presentation.internal.defaults.gradientBottomGuideLineExtended
import com.rumble.videoplayer.presentation.internal.defaults.playerBottomGuideline
import com.rumble.videoplayer.presentation.internal.defaults.playerBottomGuidelineExtended
import com.rumble.videoplayer.presentation.internal.defaults.tvSeekDuration
import com.rumble.videoplayer.presentation.internal.views.DurationView
import com.rumble.videoplayer.presentation.internal.views.LiveDurationView
import com.rumble.videoplayer.presentation.internal.views.PlayButton
import com.rumble.videoplayer.presentation.internal.views.ReplayButton
import com.rumble.videoplayer.presentation.internal.views.TvAddToPlaylistButton
import com.rumble.videoplayer.presentation.internal.views.TvChannelDetailsView
import com.rumble.videoplayer.presentation.internal.views.TvDislikeButton
import com.rumble.videoplayer.presentation.internal.views.TvLikeButton
import com.rumble.videoplayer.presentation.internal.views.TvLiveButton
import com.rumble.videoplayer.presentation.internal.views.TvSeekBar
import com.rumble.videoplayer.presentation.views.MenuType
import com.rumble.videoplayer.presentation.views.TvSettingsView
import kotlinx.coroutines.delay

private enum class Focusable {
    REPORT,
    SPEED,
    QUALITY,
    SEEK,
    REPLAY,
    LIKE,
    DISLIKE,
    ADD_TO_PLAYLIST,
    CHANNEL,
    LIVE,
    PLAYLIST
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TvControlsView(
    rumblePlayer: RumblePlayer,
    isVisible: Boolean,
    currentVote: VoteData?,
    onReport: (ReportType) -> Unit,
    onActionInProgress: (Boolean) -> Unit,
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onChannelDetailsClick: () -> Unit,
    onBack: () -> Unit,
    videoCardComposable: @Composable (video: RumbleVideo, isPlaying: Boolean, onFocused: () -> Unit, onSelection: () -> Unit) -> Unit,
) {
    val playerState by rumblePlayer.playbackState
    val totalTime by rumblePlayer.totalTime
    val currentPosition by rumblePlayer.currentPosition
    var actionInProgress by remember { mutableStateOf(false) }
    var focusedElement: Focusable by remember { mutableStateOf(Focusable.SEEK) }
    val (reportFocus, speedFocus, qualityFocus, seekFocus, replayFocus, likeFocus, dislikeFocus, addToPlaylistFocus, channelFocus, liveFocus, playListFocus) = remember { FocusRequester.createRefs() }
    var menuVisible by remember { mutableStateOf(false) }
    val isLive = rumblePlayer.isLiveVideo
    var playButtonIsVisible by remember { mutableStateOf(true) }
    val focusManager = LocalFocusManager.current
    var playListHidden by remember { mutableStateOf(true) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            seekFocus.requestFocus()
        } else {
            playListHidden = true
        }
    }

    LaunchedEffect(playListHidden) {
        if (isVisible and playListHidden) {
            seekFocus.requestFocus()
        }
    }

    LaunchedEffect(menuVisible) {
        if (!menuVisible) {
            seekFocus.requestFocus()
        }
    }

    LaunchedEffect(focusedElement, menuVisible, actionInProgress) {
        onActionInProgress(true)
        delay(controlsInactiveDelay)
        onActionInProgress(menuVisible or actionInProgress)
    }

    LaunchedEffect(playerState) {
        playButtonIsVisible = true
        delay(controlsInactiveDelay)
        playButtonIsVisible = false
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(brandedPlayerBackground.copy(0.6f))
                .onKeyEvent { event ->
                    if (event.nativeKeyEvent.keyCode != KEYCODE_BACK) {
                        handleKeyEvent(
                            rumblePlayer = rumblePlayer,
                            focusedElement = focusedElement,
                            event = event,
                            isLive = isLive,
                            playListExpended = rumblePlayer.playList != null && playListHidden.not(),
                            onSeekInProgress = { actionInProgress = it },
                            onDisplayPlayList = {
                                if (rumblePlayer.playList != null) {
                                    playListHidden = false
                                    playListFocus.requestFocus()
                                }
                            },
                            onHidePlayList = {
                                playListHidden = true
                                actionInProgress = false
                            }
                        )
                    } else {
                        if (playerState is PlayerPlaybackState.Finished) {
                            focusManager.clearFocus(force = true)
                        } else if (event.nativeKeyEvent.action == ACTION_UP) {
                            if (playListHidden.not()) {
                                playListHidden = true
                                actionInProgress = false
                            } else {
                                onBack()
                            }
                        }
                        false
                    }
                }
        ) {
            val (premiumTag, title, play, progress, duration, replay, report, speed, quality, live, channel, like, dislike, addToPlaylist, gradient, playList) = createRefs()
            val bottomOffset: Dp by animateDpAsState(
                targetValue = if (playListHidden) playerBottomGuideline else playerBottomGuidelineExtended,
                label = "bottomGuideline"
            )
            val gradientOffset: Dp by animateDpAsState(
                targetValue = if (playListHidden) gradientBottomGuideLine else gradientBottomGuideLineExtended,
                label = "gradientOffset"
            )
            val bottomGuideline = createGuidelineFromBottom(bottomOffset)
            val gradientGuideline = createGuidelineFromBottom(gradientOffset)
            val speedDisabled = rumblePlayer.enableSeekBar.not()

            Box(modifier = Modifier
                .constrainAs(gradient) {
                    top.linkTo(gradientGuideline)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            brandedPlayerBackground.copy(alpha = 0f),
                            brandedPlayerBackground
                        )
                    )
                ))

            if (playListHidden) {
                Text(
                    modifier = Modifier
                        .padding(
                            top = paddingLarge,
                            start = paddingLarge,
                            end = paddingLarge,
                            bottom = paddingXXSmall
                        )
                        .constrainAs(title) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            width = Dimension.fillToConstraints
                        },
                    text = rumblePlayer.videoTitle,
                    color = enforcedWhite,
                    style = RumbleTypography.tvH2
                )

                if ((rumblePlayer.rumbleVideo?.isPremiumExclusiveContent == true ||
                            rumblePlayer.rumbleVideo?.hasLiveGate == true) &&
                    rumblePlayer.userIsPremium != null
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = paddingLarge, vertical = paddingXXMedium)
                            .constrainAs(premiumTag) {
                                top.linkTo(parent.top)
                            }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PremiumTag(
                            modifier = Modifier
                                .conditional(rumblePlayer.userIsPremium == true) {
                                    padding(top = paddingXLarge)
                                }
                                .conditional(rumblePlayer.userIsPremium == false) {
                                    padding(bottom = paddingMedium)
                                },
                            hasLiveGate = rumblePlayer.rumbleVideo?.hasLiveGate == true
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        if (rumblePlayer.userIsPremium == false) {
                            PremiumNoteView(
                                text = if (rumblePlayer.rumbleVideo?.hasLiveGate == true) stringResource(
                                    R.string.preview_message
                                )
                                else stringResource(R.string.premium_only_content_message)
                            )
                        }
                    }
                }

                if (playerState is PlayerPlaybackState.Finished && isLive.not()) {
                    ReplayButton(
                        modifier = Modifier
                            .focusRequester(replayFocus)
                            .focusProperties {
                                down = channelFocus
                            }
                            .onFocusChanged {
                                if (it.isFocused) focusedElement = Focusable.REPLAY
                            }
                            .constrainAs(replay) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                                top.linkTo(parent.top)
                            },
                        uiType = UiType.TV,
                        isFocused = focusedElement == Focusable.REPLAY,
                        onClick = {
                            rumblePlayer.replay()
                            seekFocus.requestFocus()
                        }
                    )
                } else {
                    AnimatedVisibility(
                        modifier = Modifier
                            .focusable(false)
                            .constrainAs(play) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                                top.linkTo(parent.top)
                            },
                        visible = playButtonIsVisible,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        PlayButton(
                            rumblePlayer = rumblePlayer,
                            uiType = UiType.TV,
                            isPlayingInitial = rumblePlayer.isPlaying() || rumblePlayer.isFetching()
                        )
                    }
                }

                if (isLive.not()) {
                    DurationView(
                        modifier = Modifier
                            .padding(horizontal = paddingXLarge)
                            .constrainAs(duration) {
                                top.linkTo(progress.bottom)
                                start.linkTo(progress.start)
                            },
                        currentPosition = currentPosition.toLong(),
                        totalDuration = totalTime.toLong()
                    )
                } else if (actionInProgress) {
                    LiveDurationView(
                        modifier = Modifier
                            .constrainAs(duration) {
                                top.linkTo(live.top)
                                start.linkTo(live.end)
                            }
                            .padding(start = paddingSmall, top = paddingXXSmall),
                        currentPosition = currentPosition.toLong(),
                        totalDuration = totalTime.toLong()
                    )
                }

                TvSeekBar(
                    modifier = Modifier
                        .padding(start = paddingLarge)
                        .padding(end = paddingLarge)
                        .focusable(enabled = true)
                        .focusRequester(seekFocus)
                        .focusProperties {
                            up = channelFocus
                            if (isLive) down = liveFocus
                        }
                        .onFocusChanged {
                            if (it.isFocused) focusedElement = Focusable.SEEK
                        }
                        .constrainAs(progress) {
                            top.linkTo(bottomGuideline)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        },
                    rumblePlayer = rumblePlayer,
                    isFocused = focusedElement == Focusable.SEEK
                )

                TvSettingsView(
                    modifier = Modifier
                        .padding(end = paddingLarge)
                        .focusRequester(reportFocus)
                        .focusProperties {
                            down = seekFocus
                            left = if (speedDisabled) qualityFocus else speedFocus
                            up = replayFocus
                        }
                        .onFocusChanged {
                            if (it.isFocused) focusedElement = Focusable.REPORT
                        }
                        .constrainAs(report) {
                            end.linkTo(parent.end)
                            bottom.linkTo(progress.top)
                        },
                    rumblePlayer = rumblePlayer,
                    menuType = MenuType.REPORT,
                    isFocused = focusedElement == Focusable.REPORT,
                    onReport = onReport,
                    onMenuVisibilityChange = { menuVisible = it }
                )

                TvSettingsView(
                    modifier = Modifier
                        .conditional(speedDisabled) { alpha(0.5f) }
                        .focusRequester(speedFocus)
                        .focusProperties {
                            down = seekFocus
                            right = reportFocus
                            left = qualityFocus
                            up = replayFocus
                        }
                        .onFocusChanged {
                            if (it.isFocused) focusedElement = Focusable.SPEED
                        }
                        .constrainAs(speed) {
                            end.linkTo(report.start)
                            bottom.linkTo(progress.top)
                        }
                        .focusable(enabled = !speedDisabled),
                    rumblePlayer = rumblePlayer,
                    menuType = MenuType.SPEED_MENU,
                    isFocused = focusedElement == Focusable.SPEED,
                    onMenuVisibilityChange = { menuVisible = it }
                )

                TvSettingsView(
                    modifier = Modifier
                        .focusRequester(qualityFocus)
                        .focusProperties {
                            down = seekFocus
                            right = if (speedDisabled) reportFocus else speedFocus
                            left = addToPlaylistFocus
                            up = replayFocus
                        }
                        .onFocusChanged {
                            if (it.isFocused) focusedElement = Focusable.QUALITY
                        }
                        .constrainAs(quality) {
                            end.linkTo(speed.start)
                            bottom.linkTo(progress.top)
                        },
                    rumblePlayer = rumblePlayer,
                    menuType = MenuType.QUALITY_MENU,
                    isFocused = focusedElement == Focusable.QUALITY,
                    onMenuVisibilityChange = { menuVisible = it }
                )

                TvAddToPlaylistButton(
                    modifier = Modifier
                        .focusRequester(addToPlaylistFocus)
                        .focusProperties {
                            down = seekFocus
                            right = qualityFocus
                            left = dislikeFocus
                            up = replayFocus
                        }
                        .onFocusChanged {
                            if (it.isFocused) focusedElement = Focusable.ADD_TO_PLAYLIST
                        }
                        .constrainAs(addToPlaylist) {
                            end.linkTo(quality.start)
                            bottom.linkTo(progress.top)
                        },
                    isFocused = focusedElement == Focusable.ADD_TO_PLAYLIST,
                    onClick = onAddToPlaylist
                )

                TvDislikeButton(
                    modifier = Modifier
                        .focusRequester(dislikeFocus)
                        .focusProperties {
                            down = seekFocus
                            right = addToPlaylistFocus
                            left = likeFocus
                            up = replayFocus
                        }
                        .onFocusChanged {
                            if (it.isFocused) focusedElement = Focusable.DISLIKE
                        }
                        .constrainAs(dislike) {
                            end.linkTo(addToPlaylist.start)
                            bottom.linkTo(progress.top)
                        },
                    isFocused = focusedElement == Focusable.DISLIKE,
                    currentVote = currentVote,
                    onClick = onDislike
                )

                TvLikeButton(
                    modifier = Modifier
                        .focusRequester(likeFocus)
                        .focusProperties {
                            down = seekFocus
                            right = dislikeFocus
                            left = channelFocus
                            up = replayFocus
                        }
                        .onFocusChanged {
                            if (it.isFocused) focusedElement = Focusable.LIKE
                        }
                        .constrainAs(like) {
                            end.linkTo(dislike.start)
                            bottom.linkTo(progress.top)
                        },
                    isFocused = focusedElement == Focusable.LIKE,
                    currentVote = currentVote,
                    onClick = onLike
                )

                if (isLive) {
                    TvLiveButton(
                        modifier = Modifier
                            .focusRequester(liveFocus)
                            .focusProperties {
                                up = seekFocus
                            }
                            .onFocusChanged {
                                if (it.isFocused) focusedElement = Focusable.LIVE
                            }
                            .padding(start = paddingLarge, bottom = paddingGiant)
                            .constrainAs(live) {
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                            },
                        currentPosition = currentPosition.toLong(),
                        totalDuration = totalTime.toLong(),
                        dvrSupported = rumblePlayer.supportsDvr,
                        isFocused = focusedElement == Focusable.LIVE,
                        onSeek = { rumblePlayer.seekToPercentage(1f) },
                        onPlayPause = {
                            if (rumblePlayer.isPlaying()) rumblePlayer.pauseVideo()
                            else rumblePlayer.playVideo()
                        }
                    )
                }

                rumblePlayer.rumbleVideo?.let {
                    TvChannelDetailsView(
                        modifier = Modifier
                            .focusRequester(channelFocus)
                            .focusProperties {
                                down = seekFocus
                                right = likeFocus
                                up = replayFocus
                            }
                            .onFocusChanged { state ->
                                if (state.isFocused) focusedElement = Focusable.CHANNEL
                            }
                            .constrainAs(channel) {
                                start.linkTo(parent.start)
                                bottom.linkTo(progress.top)
                            }
                            .padding(start = paddingSmall),
                        isFocused = focusedElement == Focusable.CHANNEL,
                        onClick = onChannelDetailsClick,
                        channelName = it.channelName,
                        channelFollowers = it.channelFollowers,
                        channelIcon = it.channelIcon,
                        displayVerifiedBadge = it.displayVerifiedBadge
                    )
                }
            }

            rumblePlayer.playList?.let {
                PlayListView(
                    modifier = Modifier
                        .focusable(true)
                        .focusRequester(playListFocus)
                        .onFocusChanged { state ->
                            if (state.hasFocus) focusedElement = Focusable.PLAYLIST
                        }
                        .constrainAs(playList) {
                            top.linkTo(bottomGuideline)
                            start.linkTo(parent.start)
                            width = Dimension.fillToConstraints
                        }
                        .padding(top = paddingXXXGiant),
                    playList = it,
                    expended = playListHidden.not(),
                    currentVideoId = rumblePlayer.videoId,
                    currentSelectedIndex = rumblePlayer.lastFocusedPlayListIndex,
                    onSaveFocusSelection = { index ->
                        rumblePlayer.lastFocusedPlayListIndex = index
                    },
                    onVideoSelected = { videoId ->
                        playListHidden = true
                        actionInProgress = false
                        rumblePlayer.onPlayFromPlayList(videoId)
                    },
                    videoCardComposable = videoCardComposable
                )
            }
        }
    }
}

private fun handleKeyEvent(
    rumblePlayer: RumblePlayer,
    focusedElement: Focusable,
    event: KeyEvent,
    isLive: Boolean,
    playListExpended: Boolean,
    onSeekInProgress: (Boolean) -> Unit,
    onDisplayPlayList: () -> Unit,
    onHidePlayList: () -> Unit
): Boolean {
    return if (event.nativeKeyEvent.keyCode == KEYCODE_DPAD_DOWN && ((focusedElement == Focusable.SEEK && isLive.not()) || (focusedElement == Focusable.LIVE && isLive))) {
        if (event.nativeKeyEvent.action == ACTION_DOWN) {
            onDisplayPlayList()
            onSeekInProgress(true)
        }
        true
    } else if (event.nativeKeyEvent.keyCode == KEYCODE_MEDIA_PLAY) {
        if (event.nativeKeyEvent.action == ACTION_UP) {
            rumblePlayer.playVideo()
        }
        true
    } else if (event.nativeKeyEvent.keyCode == KEYCODE_MEDIA_PAUSE) {
        if (event.nativeKeyEvent.action == ACTION_UP) {
            rumblePlayer.pauseVideo()
        }
        true
    } else if (event.nativeKeyEvent.keyCode == KEYCODE_MEDIA_PLAY_PAUSE) {
        if (event.nativeKeyEvent.action == ACTION_UP) {
            if (rumblePlayer.isPlaying()) rumblePlayer.pauseVideo()
            else rumblePlayer.playVideo()
        }
        true
    } else if (event.nativeKeyEvent.keyCode == KEYCODE_MEDIA_FAST_FORWARD) {
        if (event.nativeKeyEvent.action == ACTION_DOWN && rumblePlayer.enableSeekBar) {
            rumblePlayer.seekForward(tvSeekDuration)
            onSeekInProgress(true)
        } else {
            onSeekInProgress(false)
        }
        true
    } else if (event.nativeKeyEvent.keyCode == KEYCODE_MEDIA_REWIND) {
        if (event.nativeKeyEvent.action == ACTION_DOWN && rumblePlayer.enableSeekBar) {
            rumblePlayer.seekBack(tvSeekDuration)
            onSeekInProgress(true)
        } else {
            onSeekInProgress(false)
        }
        true
    } else if (event.nativeKeyEvent.keyCode == KEYCODE_DPAD_LEFT && focusedElement == Focusable.SEEK) {
        if (rumblePlayer.enableSeekBar) {
            if (event.nativeKeyEvent.action == ACTION_DOWN) {
                rumblePlayer.seekBack(tvSeekDuration)
                onSeekInProgress(true)
            } else {
                onSeekInProgress(false)
            }
        }
        true
    } else if (event.nativeKeyEvent.keyCode == KEYCODE_DPAD_RIGHT && focusedElement == Focusable.SEEK) {
        if (rumblePlayer.enableSeekBar) {
            if (event.nativeKeyEvent.action == ACTION_DOWN) {
                rumblePlayer.seekForward(tvSeekDuration)
                onSeekInProgress(true)
            } else {
                onSeekInProgress(false)
            }
        }
        true
    } else if (event.nativeKeyEvent.keyCode == KEYCODE_DPAD_CENTER && focusedElement == Focusable.SEEK) {
        if (event.nativeKeyEvent.action == ACTION_UP) {
            if (rumblePlayer.isPlaying()) rumblePlayer.pauseVideo()
            else rumblePlayer.playVideo()
        }
        true
    } else if (event.nativeKeyEvent.keyCode == KEYCODE_DPAD_UP && playListExpended) {
        if (event.nativeKeyEvent.action == ACTION_UP) {
            onHidePlayList()
        }
        true
    } else {
        (event.nativeKeyEvent.keyCode == KEYCODE_DPAD_UP && rumblePlayer.isFinished().not() &&
                (focusedElement == Focusable.REPORT ||
                        focusedElement == Focusable.SPEED ||
                        focusedElement == Focusable.QUALITY ||
                        focusedElement == Focusable.LIKE ||
                        focusedElement == Focusable.DISLIKE ||
                        focusedElement == Focusable.CHANNEL)) ||
                (event.nativeKeyEvent.keyCode == KEYCODE_DPAD_RIGHT && focusedElement == Focusable.LIVE)
    }
}


