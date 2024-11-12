package com.rumble.battles.camera.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.rumble.battles.R
import com.rumble.battles.UploadPreviewTag
import com.rumble.battles.camera.presentation.views.TrimBarView
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.TransparentStatusBar
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.channelActionsButtonWidth
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageMini
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.videoTrimmerPlayHeadHeight
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.videoTrimTimerTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun VideoPreviewScreen(
    cameraHandler: CameraHandler,
    contentHandler: ContentHandler,
    uri: String,
    onNextStep: () -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val supervisorJob = SupervisorJob()
    val progressScope = CoroutineScope(Dispatchers.IO + supervisorJob)

    val uiState by cameraHandler.cameraHandlerUiState.collectAsStateWithLifecycle()

    val isPlaying: MutableState<Boolean> = remember { mutableStateOf(true) }
    val isSoundOn: MutableState<Boolean> = remember { mutableStateOf(true) }
    var initial by remember { mutableStateOf(true) }

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            seekTo(uiState.trimBarData.sliderPosition.start.toLong())
            prepare()
            playWhenReady = true
        }.apply {
            this.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            replay(this@apply, uiState)
                        }

                        Player.STATE_READY -> {
                            val endSlider =
                                if (uiState.trimBarData.sliderPosition.endInclusive == 1F) duration.toFloat() else uiState.trimBarData.sliderPosition.endInclusive
                            cameraHandler.updateSliderPosition(
                                uiState.trimBarData.sliderPosition.start..endSlider,
                                initial = initial
                            )
                            initial = false
                        }

                        else -> return
                    }
                }
            })
            listenToProgress(this, progressScope) {
                if (this.isPlaying) {
                    if (it >= uiState.trimBarData.sliderPosition.endInclusive)
                        replay(this, uiState)
                    cameraHandler.updateCurrentPosition(it)
                }
            }
        }
    }

    BackHandler {
        cameraHandler.clearTrimData()
        onBackClick()
    }

    LaunchedEffect(Unit) {
        cameraHandler.cameraHandlerEventFlow.collect { event ->
            when (event) {
                CameraHandlerVmEvent.ProceedToStepOne -> onNextStep()
                CameraHandlerVmEvent.Error -> {
                    contentHandler.onShowSnackBar(
                        messageId = R.string.generic_error_message_try_later,
                        withPadding = false
                    )
                }

                else -> {}
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    TransparentStatusBar()

    ConstraintLayout(
        modifier = Modifier
            .testTag(UploadPreviewTag)
            .fillMaxSize()
            .background(color = enforcedDarkmo)
    ) {
        val (topBar, playerView, bottomControls) = createRefs()
        RumbleBasicTopAppBar(
            title = "",
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding()
                .constrainAs(topBar) {
                    top.linkTo(parent.top)
                },
            backgroundColor = enforcedDarkmo,
            backButtonColor = enforcedWhite,
            onBackClick = {
                cameraHandler.clearTrimData()
                onBackClick()
            },
            extraContent = {
                ActionButton(
                    modifier = Modifier
                        .width(channelActionsButtonWidth)
                        .padding(end = paddingMedium),
                    text = stringResource(id = R.string.next),
                    textModifier = Modifier.padding(
                        top = paddingXXXSmall,
                        bottom = paddingXXXSmall,
                    ),
                    textColor = enforcedDarkmo
                ) {
                    cameraHandler.onNextToStepOne(uri)
                }
            }
        )
        Box(
            modifier = Modifier
                .padding(paddingLarge)
                .clip(RoundedCornerShape(radiusMedium))
                .fillMaxSize()
                .constrainAs(playerView) {
                    top.linkTo(topBar.bottom)
                    bottom.linkTo(bottomControls.top)
                    height = Dimension.fillToConstraints
                }
        ) {
            AndroidView(

                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                modifier = Modifier
                    .wrapContentSize()
                    .clip(RoundedCornerShape(radiusMedium))
                    .align(Alignment.Center)
            )
        }
        Column(
            modifier = Modifier
                .systemGestureExclusion()
                .fillMaxWidth()
                .padding(
                    start = paddingLarge,
                    end = paddingLarge,
                    bottom = paddingXLarge,
                )
                .constrainAs(bottomControls) {
                    top.linkTo(playerView.bottom)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = paddingLarge)
            ) {
                Icon(
                    painter = painterResource(id = if (isPlaying.value) R.drawable.ic_pause else R.drawable.ic_play_fullscreen),
                    contentDescription = stringResource(id = if (isPlaying.value) R.string.pause else R.string.play),
                    modifier = Modifier
                        .size(imageXXSmall)
                        .align(Alignment.CenterStart)
                        .clickable {
                            if (isPlaying.value) {
                                exoPlayer.pause()
                                cameraHandler.updateLoopVideoState(false)
                            } else {
                                cameraHandler.updateLoopVideoState(true)
                                exoPlayer.play()
                            }
                            isPlaying.value = isPlaying.value.not()
                        },
                    tint = enforcedWhite
                )
                Icon(
                    painter = painterResource(id = if (isSoundOn.value) R.drawable.ic_sound_on else R.drawable.ic_sound_off),
                    contentDescription = stringResource(id = if (isSoundOn.value) R.string.sound_on else R.string.sound_off),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable {
                            if (isSoundOn.value) exoPlayer.volume = 0F else exoPlayer.volume =
                                1F
                            isSoundOn.value = isSoundOn.value.not()
                        },
                    tint = enforcedWhite
                )
            }
            val duration = exoPlayer.duration
            if (duration > 0) {
                cameraHandler.updateDuration(duration)
                TrimBarView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(videoTrimmerPlayHeadHeight)
                        .onGloballyPositioned {
                            if (uiState.trimThumbnails.isEmpty())
                                cameraHandler.generateTrimThumbnails(
                                    uri,
                                    it.size,
                                )
                        },
                    cameraHandler = cameraHandler,
                    exoPlayer = exoPlayer,
                    trimBarData = uiState.trimBarData,
                    trimThumbnails = uiState.trimThumbnails,
                    duration = exoPlayer.duration,
                    onCurrentPositionChange = {
                        cameraHandler.updateCurrentPosition(it)
                        exoPlayer.seekTo(it.toLong())
                    },
                )
                VideoTrimTimesView(exoPlayer.duration)
            }
        }
    }

    if (uiState.loading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

private fun replay(exoPlayer: ExoPlayer, uiState: CameraHandlerUIState) {
    exoPlayer.seekTo(uiState.trimBarData.sliderPosition.start.toLong())
    exoPlayer.playWhenReady = true
}

@Composable
private fun VideoTrimTimesView(duration: Long) {
    val start = 0L.videoTrimTimerTime()
    val middle = (duration / 2).videoTrimTimerTime()
    val end = duration.videoTrimTimerTime()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = start, color = enforcedBone, style = h6)
        repeat((1..4).count()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dot),
                contentDescription = stringResource(id = R.string.duration),
                modifier = Modifier.size(imageMini),
                tint = enforcedBone
            )
        }
        Text(text = middle, color = enforcedBone, style = h6)
        repeat((1..4).count()) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dot),
                contentDescription = stringResource(id = R.string.duration),
                modifier = Modifier.size(imageMini),
                tint = enforcedBone
            )
        }
        Text(text = end, color = enforcedBone, style = h6)
    }
}

private fun listenToProgress(
    exoPlayer: ExoPlayer, progressScope: CoroutineScope,
    onUpdateCurrentPosition: (value: Float) -> Unit
) {
    progressScope.launch {
        while (isActive) {
            delay(RumbleConstants.PLAYER_STATE_UPDATE_RATIO)
            withContext(Dispatchers.Main) {
                onUpdateCurrentPosition(exoPlayer.currentPosition.coerceAtLeast(0L).toFloat())
            }
        }
    }
}