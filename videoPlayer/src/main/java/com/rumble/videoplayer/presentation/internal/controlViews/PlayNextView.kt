package com.rumble.videoplayer.presentation.internal.controlViews

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h2
import com.rumble.theme.RumbleTypography.h3Normal
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.RumbleTypography.tinyBodyExtraBold
import com.rumble.theme.RumbleTypography.tinyBodySemiBold8dp
import com.rumble.theme.borderXSmall
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.compactVideoHeight
import com.rumble.theme.compactVideoWidth
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXLarge
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.parsedTime
import com.rumble.utils.extension.shortString
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumbleVideo
import com.rumble.videoplayer.player.config.RumbleVideoMode
import com.rumble.videoplayer.player.config.StreamStatus
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.playNexTvHeight
import com.rumble.videoplayer.presentation.internal.defaults.playNexTvWidth
import com.rumble.videoplayer.presentation.internal.defaults.playNextAppearanceDelay
import com.rumble.videoplayer.presentation.internal.defaults.playNextCloseSize
import com.rumble.videoplayer.presentation.internal.defaults.playNextDelay
import com.rumble.videoplayer.presentation.internal.defaults.playNextWidthFrame
import com.rumble.videoplayer.presentation.internal.defaults.playNextWidthFrameTv
import com.rumble.videoplayer.presentation.internal.defaults.upNextDescriptionMaxLines
import com.rumble.videoplayer.presentation.internal.views.PlayerActionButton
import kotlinx.coroutines.delay

private enum class FocusedAction {
    PLAY,
    CANCEL
}

@Composable
fun PlayNextView(
    uiType: UiType,
    rumbleVideo: RumbleVideo,
    rumbleVideoMode: RumbleVideoMode,
    delayInitialCount: Int,
    onPlayNextCountChanged: (Int) -> Unit,
    onCancel: () -> Unit,
    onPlayNow: () -> Unit
) {
    var delayCount by rememberSaveable { mutableIntStateOf(delayInitialCount) }
    val isLiveVideo = remember {
        rumbleVideo.streamStatus == StreamStatus.LiveStream
            || rumbleVideo.streamStatus == StreamStatus.OfflineStream
    }
    var contentIsVisible by rememberSaveable { mutableStateOf(false) }
    var cancelNow by rememberSaveable { mutableStateOf(false) }
    var playNow by rememberSaveable { mutableStateOf(false) }
    var currentVideoMode by remember { mutableStateOf(rumbleVideoMode) }

    LaunchedEffect(rumbleVideoMode) {
        currentVideoMode = rumbleVideoMode
    }

    LaunchedEffect(Unit) {
        delay(playNextAppearanceDelay)
        contentIsVisible = true
        while (delayCount > 0) {
            delay(playNextDelay)
            if (currentVideoMode != RumbleVideoMode.BackgroundPaused) {
                delayCount--
                onPlayNextCountChanged(delayCount)
            }
        }
        playNow = true
    }

    LaunchedEffect(cancelNow) {
        if (cancelNow) {
            contentIsVisible = false
            delay(playNextAppearanceDelay)
            onCancel()
        }
    }

    LaunchedEffect(playNow) {
        if (playNow) {
            contentIsVisible = false
            delay(playNextAppearanceDelay)
            onPlayNow()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brandedPlayerBackground.copy(0.7f))
    ) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = contentIsVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (currentVideoMode == RumbleVideoMode.Minimized) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .focusable(enabled = false),
                        text = "$delayCount",
                        style = RumbleTypography.h1,
                        color = enforcedWhite
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .conditional(uiType != UiType.TV) {
                                widthIn(max = playNextWidthFrame)
                            }
                            .conditional(uiType == UiType.TV) {
                                wrapContentSize()
                            }
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(if (uiType == UiType.TV) paddingXXLarge else paddingMedium)
                    ) {
                        Text(
                            modifier = Modifier.focusable(enabled = false),
                            text = stringResource(id = R.string.up_next_in) + " $delayCount",
                            style = if (uiType == UiType.TV) RumbleTypography.tvH2 else RumbleTypography.h3,
                            color = enforcedWhite
                        )

                        VideoInfoView(
                            modifier = Modifier.focusable(enabled = false),
                            uiType = uiType,
                            rumbleVideo = rumbleVideo,
                            isLiveVideo = isLiveVideo
                        )

                        ActionsView(
                            uiType = uiType,
                            onCancel = { cancelNow = true },
                            onPlayNow = { playNow = true }
                        )
                    }

                    if (uiType != UiType.TV) {
                        IconButton(
                            modifier = Modifier
                                .padding(paddingMedium)
                                .size(playNextCloseSize)
                                .align(Alignment.TopEnd),
                            onClick = { cancelNow = true }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = stringResource(id = R.string.close),
                                tint = enforcedWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoInfoView(
    modifier: Modifier,
    uiType: UiType,
    rumbleVideo: RumbleVideo,
    isLiveVideo: Boolean
) {
    if (uiType == UiType.TV) {
        Column(
            modifier = modifier.width(playNextWidthFrameTv),
            verticalArrangement = Arrangement.spacedBy(paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ThumbnailView(
                rumbleVideo = rumbleVideo,
                uiType = uiType,
                isLiveVideo = isLiveVideo
            )
            InfoView(
                rumbleVideo = rumbleVideo,
                uiType = uiType
            )
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(paddingMedium)) {
            ThumbnailView(
                rumbleVideo = rumbleVideo,
                uiType = uiType,
                isLiveVideo = isLiveVideo
            )
            InfoView(
                rumbleVideo = rumbleVideo,
                uiType = uiType
            )
        }
    }
}

@Composable
private fun ThumbnailView(
    modifier: Modifier = Modifier,
    rumbleVideo: RumbleVideo,
    uiType: UiType,
    isLiveVideo: Boolean
) {
    Box(
        modifier = modifier
            .height(if (uiType == UiType.TV) playNexTvHeight else compactVideoHeight)
            .width(if (uiType == UiType.TV) playNexTvWidth else compactVideoWidth)
            .wrapContentHeight()
            .clip(RoundedCornerShape(radiusMedium))
            .background(MaterialTheme.colors.primaryVariant)
            .conditional(isLiveVideo) {
                border(
                    width = borderXSmall,
                    color = fierceRed,
                    shape = RoundedCornerShape(radiusMedium)
                )
            }
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = rumbleVideo.videoThumbnailUri,
            contentDescription = "",
            contentScale = ContentScale.FillWidth
        )

        if (isLiveVideo) {
            WatchingView(
                modifier = Modifier
                    .padding(paddingXSmall)
                    .align(Alignment.BottomStart),
                rumbleVideo = rumbleVideo,
                uiType = uiType
            )
            LiveTagView(
                modifier = Modifier
                    .padding(paddingXSmall)
                    .align(Alignment.BottomEnd),
                uiType = uiType
            )
        } else {
            DurationTagView(
                modifier = Modifier
                    .padding(paddingXSmall)
                    .align(Alignment.BottomStart),
                duration = rumbleVideo.duration,
                uiType = uiType
            )
        }
    }
}

@Composable
private fun InfoView(
    modifier: Modifier = Modifier,
    rumbleVideo: RumbleVideo,
    uiType: UiType
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = rumbleVideo.title,
            style = if (uiType == UiType.TV) h2 else h6,
            color = enforcedWhite,
            maxLines = upNextDescriptionMaxLines,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = rumbleVideo.channelName,
            style = if (uiType == UiType.TV) h3Normal else h6Light,
            color = enforcedWhite
        )
    }
}

@Composable
private fun DurationTagView(
    modifier: Modifier = Modifier,
    duration: Long,
    uiType: UiType
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusSmall))
            .background(enforcedDarkmo)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = paddingXSmall, vertical = paddingXXXSmall),
            text = duration.parsedTime(),
            style = if (uiType == UiType.TV) h4 else RumbleTypography.tinyBodySemiBold,
            color = enforcedWhite
        )
    }
}

@Composable
private fun WatchingView(
    modifier: Modifier = Modifier,
    rumbleVideo: RumbleVideo,
    uiType: UiType
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusSmall))
            .background(enforcedDarkmo)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = paddingXSmall, vertical = paddingXXXSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.padding(end = paddingXXSmall),
                painter = painterResource(id = R.drawable.ic_player_views),
                contentDescription = stringResource(id = R.string.live),
            )

            Text(
                text = rumbleVideo.watchingNow.shortString(),
                style = if (uiType == UiType.TV) h4 else tinyBodySemiBold8dp,
                color = enforcedWhite
            )
        }
    }
}

@Composable
private fun LiveTagView(
    modifier: Modifier = Modifier,
    uiType: UiType
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusSmall))
            .background(fierceRed)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = paddingXSmall, vertical = paddingXXXSmall),
            text = stringResource(id = R.string.live).uppercase(),
            style = if (uiType == UiType.TV) h4 else tinyBodyExtraBold,
            color = enforcedWhite
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ActionsView(
    modifier: Modifier = Modifier,
    uiType: UiType,
    onCancel: () -> Unit,
    onPlayNow: () -> Unit
) {
    val (cancelFocus, playFocus) = remember { FocusRequester.createRefs() }
    LaunchedEffect(Unit) {
        if (uiType == UiType.TV) playFocus.requestFocus()
    }
    var playFocused by remember { mutableStateOf(FocusedAction.PLAY) }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier.onKeyEvent { event ->
            if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK) {
                if (uiType == UiType.TV) {
                    focusManager.clearFocus()
                }
            }
            false
        },
        horizontalArrangement = Arrangement.spacedBy(paddingLarge)
    ) {
        PlayerActionButton(
            modifier = Modifier
                .focusable(enabled = true)
                .focusRequester(cancelFocus)
                .onFocusChanged {
                    playFocused = FocusedAction.CANCEL
                }
                .focusProperties {
                    right = playFocus
                },
            backgroundColor = enforcedFiord.copy(alpha = 0.6f),
            text = stringResource(id = R.string.cancel),
            textColor = enforcedWhite,
            isFocused = playFocused == FocusedAction.CANCEL,
            uiType = uiType,
            action = onCancel
        )

        PlayerActionButton(
            modifier = Modifier
                .focusable(enabled = true)
                .focusRequester(playFocus)
                .onFocusChanged {
                    playFocused = FocusedAction.PLAY
                }
                .focusProperties {
                    left = cancelFocus
                },
            backgroundColor = rumbleGreen,
            text = stringResource(id = R.string.play_now),
            textColor = enforcedDarkmo,
            isFocused = playFocused == FocusedAction.PLAY,
            uiType = uiType,
            action = onPlayNow
        )
    }
}