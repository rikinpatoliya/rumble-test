package com.rumble.videoplayer.presentation.views

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedGray950
import com.rumble.theme.enforcedWhite
import com.rumble.theme.modalMaxWidth
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXGiant
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.radiusXXXMedium
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlaybackSpeed
import com.rumble.videoplayer.player.config.PlayerVideoSource
import com.rumble.videoplayer.presentation.internal.defaults.tickPadding
import com.rumble.videoplayer.presentation.internal.defaults.tickSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

interface SettingsBottomSheetHandler {
    val autoplayFlow: Flow<Boolean>

    fun onDismissBottomSheet()
    fun onReport()
    fun onAutoplayOn(on: Boolean)
}

@Composable
fun VideoSettingsBottomSheet(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer,
    settingsBottomSheetHandler: SettingsBottomSheetHandler,
    isTablet: Boolean
) {
    val configuration = LocalConfiguration.current
    val detached = isTablet && configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Box(
        modifier = modifier
            .conditional(detached) {
                padding(bottom = paddingXXGiant)
            }
            .conditional(detached) {
                clip(RoundedCornerShape(bottomStart = radiusXMedium, bottomEnd = radiusXMedium))
            }
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .sizeIn(modalMaxWidth)
            .background(enforcedGray950.copy(alpha = 0.9f))
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            CloseView(
                modifier = Modifier.fillMaxWidth(),
                onClose = settingsBottomSheetHandler::onDismissBottomSheet
            )

            ReportView(
                modifier = Modifier.fillMaxWidth(),
                onReport = settingsBottomSheetHandler::onReport
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingLarge),
                color = enforcedWhite.copy(0.2f)
            )

            if (rumblePlayer.autoPlayEnabled) {
                AutoplayView(
                    modifier = Modifier.fillMaxWidth(),
                    settingsBottomSheetHandler = settingsBottomSheetHandler
                )

                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingLarge),
                    color = enforcedWhite.copy(0.2f)
                )
            }

            SoundView(
                modifier = Modifier.fillMaxWidth(),
                rumblePlayer = rumblePlayer
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingLarge),
                color = enforcedWhite.copy(0.2f)
            )

            VideoSpeedView(
                modifier = modifier.fillMaxWidth(),
                rumblePlayer = rumblePlayer
            )

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingLarge),
                color = enforcedWhite.copy(0.2f)
            )

            VideoQualityView(
                modifier = Modifier.fillMaxSize(),
                rumblePlayer = rumblePlayer
            )
        }
    }
}

@Composable
private fun CloseView(
    modifier: Modifier,
    onClose: () -> Unit
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.End) {
        IconButton(
            modifier = Modifier.padding(end = paddingMedium, top = paddingMedium),
            onClick = onClose
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.close),
                tint = enforcedWhite
            )
        }
    }
}

@Composable
private fun ReportView(
    modifier: Modifier,
    onReport: () -> Unit
) {
    Row(
        modifier = modifier.clickable { onReport() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = paddingLarge, end = paddingMedium),
            painter = painterResource(id = R.drawable.ic_flag),
            contentDescription = stringResource(id = R.string.report),
            tint = enforcedWhite
        )

        Text(
            text = stringResource(id = R.string.report),
            style = h4,
            color = enforcedWhite
        )
    }
}

@Composable
private fun SoundView(
    modifier: Modifier,
    rumblePlayer: RumblePlayer
) {
    val isMuted by remember { rumblePlayer.isMuted }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = paddingLarge, end = paddingMedium),
            painter = if (isMuted.not()) painterResource(id = R.drawable.ic_sound_on)
            else painterResource(id = R.drawable.ic_sound_off),
            contentDescription = stringResource(id = R.string.report),
            tint = enforcedWhite
        )

        Text(
            text = stringResource(id = R.string.sound),
            style = h4,
            color = enforcedWhite
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            modifier = Modifier.padding(end = paddingLarge),
            checked = isMuted.not(),
            colors = SwitchDefaults.colors(
                checkedTrackColor = rumbleGreen,
            ),
            onCheckedChange = {
                if (it) rumblePlayer.unMute() else rumblePlayer.mute()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VideoSpeedView(
    modifier: Modifier,
    rumblePlayer: RumblePlayer
) {
    var selection by remember { mutableFloatStateOf(rumblePlayer.getCurrentSpeed().stepValue) }
    val enabled = rumblePlayer.enableSeekBar

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = paddingLarge, end = paddingLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(end = paddingMedium),
                painter = painterResource(id = R.drawable.ic_speed),
                contentDescription = stringResource(id = R.string.report),
                tint = enforcedWhite
            )

            Text(
                text = stringResource(id = R.string.speed),
                style = h4,
                color = enforcedWhite
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = rumblePlayer.getCurrentSpeed().title,
                style = h4,
                color = if (enabled) rumbleGreen else enforcedWhite
            )
        }

        Box(
            modifier = Modifier.padding(
                start = paddingLarge,
                end = paddingLarge,
                top = paddingSmall
            )
        ) {

            RumbleTrack(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                enabled = enabled
            )

            SpeedValuesView(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                enabled = enabled
            )

            if (enabled) {
                Slider(
                    modifier = Modifier.fillMaxWidth(),
                    value = selection,
                    onValueChange = { value ->
                        rumblePlayer.setPlaybackSpeed(PlaybackSpeed.getByStepValue(value))
                        selection = value
                    },
                    valueRange = 0f..(PlaybackSpeed.values().size - 1).toFloat(),
                    steps = PlaybackSpeed.values().size - 2,
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent,
                        disabledActiveTrackColor = Color.Transparent,
                        disabledInactiveTrackColor = Color.Transparent,
                    ),
                    thumb = {
                        Icon(
                            modifier = Modifier.align(Alignment.Center),
                            painter = painterResource(id = R.drawable.video_speed_indicator),
                            contentDescription = "",
                            tint = rumbleGreen
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun RumbleTrack(
    modifier: Modifier,
    enabled: Boolean
) {
    Box(
        modifier = modifier.height(tickSize)
    ) {
        val emptyIndex =
            PlaybackSpeed.values().filter { it.stepLabel.isNotEmpty() }.map { it.ordinal }
        var doteColor = Color(enforcedWhite.toArgb())
        if (enabled.not()) doteColor = doteColor.copy(0.5f)
        val drawPadding: Float = with(LocalDensity.current) { tickPadding.toPx() }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val distance: Float =
                (size.width.minus(2 * drawPadding)).div(PlaybackSpeed.values().size.minus(1))
            PlaybackSpeed.values().forEachIndexed { index, _ ->
                if (emptyIndex.contains(index).not()) {
                    drawCircle(
                        color = doteColor,
                        center = Offset(
                            x = drawPadding + index.times(distance),
                            y = center.y
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedValuesView(
    modifier: Modifier,
    enabled: Boolean
) {
    val color =
        if (enabled) enforcedWhite else enforcedWhite.copy(0.5f)
    Row(modifier = modifier) {
        Text(
            text = PlaybackSpeed.SPEED_0_25.stepLabel,
            style = h6,
            color = color
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier.padding(end = paddingXXXSmall),
            text = PlaybackSpeed.NORMAL.stepLabel,
            style = h6,
            color = color
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier.padding(end = paddingXXXSmall),
            text = PlaybackSpeed.SPEED_2.stepLabel,
            style = h6,
            color = color
        )
    }
}

@Composable
private fun VideoQualityView(
    modifier: Modifier,
    rumblePlayer: RumblePlayer
) {
    var selection by remember { mutableStateOf(rumblePlayer.getCurrentVideoSource()) }
    val listState = rememberLazyListState()

    LaunchedEffect(selection) {
        val index = rumblePlayer.getSourceList().reversed().indexOf(selection)
        if (index >= 0) {
            listState.animateScrollToItem(index)
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = paddingLarge),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(end = paddingMedium),
                painter = painterResource(id = R.drawable.ic_tv_quality),
                contentDescription = stringResource(id = R.string.report),
                tint = enforcedWhite
            )

            Text(
                text = stringResource(id = R.string.video_quality),
                style = h4,
                color = enforcedWhite
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = paddingLarge),
            horizontalArrangement = Arrangement.spacedBy(paddingSmall),
            contentPadding = PaddingValues(horizontal = paddingLarge),
            state = listState
        ) {
            rumblePlayer.getSourceList().reversed().forEach {
                item {
                    VideoQualityButton(
                        videoSource = it,
                        selected = it == selection,
                        onClick = {
                            selection = it
                            rumblePlayer.setCurrentVideoSource(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoQualityButton(
    modifier: Modifier = Modifier,
    videoSource: PlayerVideoSource,
    selected: Boolean,
    onClick: (PlayerVideoSource) -> Unit
) {
    val qualityText = videoSource.qualityText?.replaceFirstChar { it.uppercase() }
        ?: stringResource(id = R.string.auto)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusXXXMedium))
            .background(
                if (selected) enforcedWhite
                else enforcedWhite.copy(0.1f)
            )
            .clickable { onClick(videoSource) }
    ) {
        Column(
            modifier = Modifier.padding(vertical = paddingXSmall, horizontal = paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            videoSource.bitrateText?.let {
                Text(
                    text = qualityText,
                    style = tinyBody,
                    color = if (selected) enforcedDarkmo else enforcedWhite
                )

                Text(
                    text = it,
                    style = tinyBody,
                    color = if (selected) enforcedFiord else enforcedCloud
                )
            } ?: run {
                Text(
                    modifier = Modifier.padding(vertical = paddingXXSmall),
                    text = qualityText,
                    style = tinyBody,
                    color = if (selected) enforcedDarkmo else enforcedWhite
                )
            }
        }
    }
}

@Composable
private fun AutoplayView(
    modifier: Modifier,
    settingsBottomSheetHandler: SettingsBottomSheetHandler
) {
    var autoplayOn by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        settingsBottomSheetHandler.autoplayFlow.collectLatest {
            autoplayOn = it
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(start = paddingLarge, end = paddingMedium),
            painter = painterResource(id = R.drawable.ic_autoplay),
            contentDescription = stringResource(id = R.string.autoplay_next_video),
            tint = enforcedWhite
        )

        Text(
            text = stringResource(id = R.string.autoplay_next_video),
            style = h4,
            color = enforcedWhite
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            modifier = Modifier.padding(end = paddingLarge),
            checked = autoplayOn,
            colors = SwitchDefaults.colors(
                checkedTrackColor = rumbleGreen,
            ),
            onCheckedChange = {
                autoplayOn = autoplayOn.not()
                settingsBottomSheetHandler.onAutoplayOn(autoplayOn)
            }
        )
    }
}

