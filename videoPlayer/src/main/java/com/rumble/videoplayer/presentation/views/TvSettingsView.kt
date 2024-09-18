package com.rumble.videoplayer.presentation.views

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.theme.RumbleTvTypography
import com.rumble.theme.RumbleTypography.h6Bold
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.tvPlayerModalWidth
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.player.config.PlaybackSpeed
import com.rumble.videoplayer.player.config.ReportType

enum class MenuType(val descriptionId: Int) {
    REPORT(R.string.report),
    QUALITY_MENU(R.string.quality),
    SPEED_MENU(R.string.speed)
}

private enum class QualityGroup(val minValue: Int) {
    HD(720),
    UHD(1440)
}

private sealed class Focusable {
    object Back : Focusable()
    data class List(val index: Int) : Focusable()
}

@Composable
fun TvSettingsView(
    modifier: Modifier,
    rumblePlayer: RumblePlayer,
    menuType: MenuType,
    isFocused: Boolean,
    onReport: (ReportType) -> Unit = {},
    onMenuVisibilityChange: (Boolean) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        onMenuVisibilityChange(expanded)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (isFocused) {
            Text(
                text = stringResource(id = menuType.descriptionId),
                style = h6Bold,
                color = enforcedWhite
            )

            Spacer(modifier = Modifier.height(paddingSmall))
        }

        Box(modifier = Modifier
            .clip(CircleShape)
            .clickable {
                expanded = true
            }
            .conditional(isFocused) {
                background(enforcedWhite.copy(alpha = 0.2f))
            }) {

            MenuIcon(rumblePlayer = rumblePlayer, menuType = menuType)

            if (expanded) {
                when (menuType) {
                    MenuType.QUALITY_MENU -> {
                        QualityDialog(
                            rumblePlayer = rumblePlayer,
                            onDismiss = { expanded = false }
                        )
                    }

                    MenuType.SPEED_MENU -> {
                        SpeedDialog(
                            rumblePlayer = rumblePlayer,
                            onDismiss = { expanded = false }
                        )
                    }

                    MenuType.REPORT -> {
                        ReportDialog(
                            onReport = {
                                onReport(it)
                                expanded = false
                            },
                            onDismiss = { expanded = false })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun BaseSettingDialog(
    title: String,
    onDismiss: () -> Unit,
    backFocusRequester: FocusRequester,
    backFocused: Boolean,
    onBackFocused: () -> Unit,
    listContent: LazyListScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
        dialogWindowProvider.window.setGravity(Gravity.END)

        ConstraintLayout(
            modifier =
            Modifier
                .padding(paddingSmall)
                .fillMaxHeight()
                .wrapContentWidth()
        ) {
            val (back, window) = createRefs()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(tvPlayerModalWidth)
                    .constrainAs(window) {
                        start.linkTo(back.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .background(
                        color = enforcedDarkmo,
                        shape = RoundedCornerShape(radiusSmall)
                    )
            ) {

                Text(
                    modifier = Modifier.padding(
                        top = paddingSmall,
                        start = paddingSmall,
                        end = paddingSmall,
                        bottom = paddingXXMedium
                    ),
                    text = title,
                    style = RumbleTvTypography.h3Tv,
                    color = enforcedWhite
                )

                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = paddingMedium)
                        .focusProperties {
                            exit = { focusDirection ->
                                if (focusDirection == FocusDirection.Left || focusDirection == FocusDirection.Up) backFocusRequester else FocusRequester.Default
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(paddingSmall),
                ) {
                    listContent()
                }
            }

            Icon(
                modifier = Modifier
                    .focusTarget()
                    .focusRequester(backFocusRequester)
                    .onFocusEvent { if (it.isFocused) onBackFocused() }
                    .constrainAs(back) {
                        end.linkTo(window.start)
                        top.linkTo(window.top)
                    }
                    .padding(paddingMedium)
                    .clickable { onDismiss() },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = stringResource(id = R.string.back),
                tint = if (backFocused) rumbleGreen else enforcedWhite
            )
        }
    }
}

@Composable
private fun QualityDialog(
    rumblePlayer: RumblePlayer,
    onDismiss: () -> Unit,
) {
    var selection by remember { mutableStateOf(rumblePlayer.getCurrentVideoSource()) }
    var focused: Focusable by remember { mutableStateOf(Focusable.Back) }
    val backFocusRequester = remember { FocusRequester() }
    val listFocusRequesters = remember { mutableMapOf<Int, FocusRequester>() }

    BaseSettingDialog(
        title = stringResource(id = R.string.quality),
        onDismiss = onDismiss,
        backFocusRequester = backFocusRequester,
        backFocused = focused == Focusable.Back,
        onBackFocused = { focused = Focusable.Back }
    ) {
        itemsIndexed(rumblePlayer.getSourceList().reversed()) { index, videoSource ->
            val focusRequester =
                listFocusRequesters.getOrPut(index) { FocusRequester() }

            Box(modifier = Modifier
                .background(
                    color = if (focused == Focusable.List(index)) enforcedWhite.copy(
                        alpha = .1f
                    ) else Color.Transparent,
                    shape = RoundedCornerShape(radiusSmall)
                )
                .focusRequester(focusRequester)
                .onFocusEvent {
                    if (it.isFocused || it.hasFocus) {
                        focused = Focusable.List(index)
                    }
                }
                .clickable {
                    selection = videoSource
                    rumblePlayer.setCurrentVideoSource(videoSource)
                    onDismiss()
                })
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = paddingSmall, horizontal = paddingMedium)

                ) {
                    val textColor = if (videoSource == selection) rumbleGreen else enforcedWhite
                    Text(
                        text = videoSource.qualityText?.replaceFirstChar { it.uppercase() }
                            ?: stringResource(id = R.string.auto),
                        style = RumbleTvTypography.h5Tv,
                        color = textColor
                    )
                    videoSource.bitrateText?.let {
                        Spacer(modifier = Modifier.height(paddingXXXXSmall))
                        Text(
                            text = videoSource.bitrateText,
                            style = RumbleTvTypography.labelRegularTv,
                            color = textColor
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        listFocusRequesters[0]?.requestFocus()
    }
}

@Composable
private fun SpeedDialog(
    rumblePlayer: RumblePlayer,
    onDismiss: () -> Unit,
) {
    var selection by remember { mutableStateOf(rumblePlayer.getCurrentSpeed()) }
    var focused: Focusable by remember { mutableStateOf(Focusable.Back) }
    val backFocusRequester = remember { FocusRequester() }
    val listFocusRequesters = remember { mutableMapOf<Int, FocusRequester>() }

    BaseSettingDialog(
        title = stringResource(id = R.string.speed),
        onDismiss = onDismiss,
        backFocusRequester = backFocusRequester,
        backFocused = focused == Focusable.Back,
        onBackFocused = { focused = Focusable.Back }
    ) {
        itemsIndexed(PlaybackSpeed.values()) { index, playbackSpeed ->
            val focusRequester =
                listFocusRequesters.getOrPut(index) { FocusRequester() }

            Box(modifier = Modifier
                .background(
                    color = if (focused == Focusable.List(index)) enforcedWhite.copy(
                        alpha = .1f
                    ) else Color.Transparent,
                    shape = RoundedCornerShape(radiusSmall)
                )
                .focusRequester(focusRequester)
                .onFocusEvent {
                    if (it.isFocused || it.hasFocus) {
                        focused = Focusable.List(index)
                    }
                }
                .clickable {
                    selection = playbackSpeed
                    rumblePlayer.setPlaybackSpeed(playbackSpeed)
                    onDismiss()
                })
            {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            vertical = paddingSmall,
                            horizontal = paddingMedium
                        )
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = playbackSpeed.title,
                        style = RumbleTvTypography.h5Tv,
                        color = if (playbackSpeed == selection) rumbleGreen else enforcedWhite
                    )
                    if (playbackSpeed == selection) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = stringResource(id = R.string.check),
                            tint = rumbleGreen
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        listFocusRequesters[PlaybackSpeed.values().indexOf(selection)]?.requestFocus()
    }
}

@Composable
private fun ReportDialog(
    onReport: (ReportType) -> Unit,
    onDismiss: () -> Unit,
) {
    var focused: Focusable by remember { mutableStateOf(Focusable.Back) }
    val backFocusRequester = remember { FocusRequester() }
    val listFocusRequesters = remember { mutableMapOf<Int, FocusRequester>() }

    BaseSettingDialog(
        title = stringResource(id = R.string.report),
        onDismiss = onDismiss,
        backFocusRequester = backFocusRequester,
        backFocused = focused == Focusable.Back,
        onBackFocused = { focused = Focusable.Back }
    ) {
        itemsIndexed(ReportType.values()) { index, reportType ->
            val focusRequester =
                listFocusRequesters.getOrPut(index) { FocusRequester() }

            Box(modifier = Modifier
                .background(
                    color = if (focused == Focusable.List(index)) enforcedWhite.copy(
                        alpha = .1f
                    ) else Color.Transparent,
                    shape = RoundedCornerShape(radiusSmall)
                )
                .focusRequester(focusRequester)
                .onFocusEvent {
                    if (it.isFocused || it.hasFocus) {
                        focused = Focusable.List(index)
                    }
                }
                .clickable {
                    onReport(reportType)
                })
            {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            vertical = paddingSmall,
                            horizontal = paddingMedium
                        ),
                    text = stringResource(id = reportType.value),
                    style = RumbleTvTypography.h5Tv,
                    color = enforcedWhite
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        listFocusRequesters[0]?.requestFocus()
    }
}

@Composable
private fun MenuIcon(rumblePlayer: RumblePlayer, menuType: MenuType) {
    val currentQuality = rumblePlayer.getCurrentVideoSource()?.resolution ?: 0
    val iconId: Int = if (menuType == MenuType.REPORT) {
        R.drawable.ic_flag
    } else if (menuType == MenuType.SPEED_MENU) {
        R.drawable.ic_speed
    } else {
        if (currentQuality >= QualityGroup.UHD.minValue)
            R.drawable.ic_quality_uhd
        else if (currentQuality >= QualityGroup.HD.minValue)
            R.drawable.ic_quality_hd
        else R.drawable.ic_quality_sd
    }

    Icon(
        modifier = Modifier
            .padding(paddingMedium),
        painter = painterResource(id = iconId),
        contentDescription = stringResource(id = menuType.descriptionId),
        tint = Color.White
    )
}