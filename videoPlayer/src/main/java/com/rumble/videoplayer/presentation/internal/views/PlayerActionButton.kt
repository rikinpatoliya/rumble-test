package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h1Bold
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedWhite
import com.rumble.theme.radiusLarge
import com.rumble.theme.rumbleGreen
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.playerActionButtonHeight
import com.rumble.videoplayer.presentation.internal.defaults.playerActionButtonHeightTv
import com.rumble.videoplayer.presentation.internal.defaults.playerActionButtonWidth
import com.rumble.videoplayer.presentation.internal.defaults.playerActionButtonWidthTv

@Composable
internal fun PlayerActionButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    text: String,
    textColor: Color,
    uiType: UiType,
    isFocused: Boolean = false,
    action: () -> Unit
) {
    if (uiType == UiType.TV) {
        Box(
            modifier = modifier
                .width(playerActionButtonWidthTv)
                .height(playerActionButtonHeightTv)
                .clip(RoundedCornerShape(radiusLarge))
                .background(if (isFocused) rumbleGreen else enforcedFiord.copy(alpha = 0.6f))
                .clickable { action() }
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                color = if (isFocused) enforcedDarkmo else enforcedWhite,
                style = h1Bold
            )
        }
    } else {
        Box(
            modifier = modifier
                .width(playerActionButtonWidth)
                .height(playerActionButtonHeight)
                .clip(RoundedCornerShape(radiusLarge))
                .background(backgroundColor)
                .clickable { action() }
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                color = textColor,
                style = h6
            )
        }
    }
}

@Composable
@Preview
private fun PreviewCancel() {
    RumbleTheme {
        PlayerActionButton(
            backgroundColor = enforcedFiord.copy(alpha = 0.6f),
            text = "Cancel",
            textColor = enforcedWhite,
            uiType = UiType.DISCOVER,
            action = {}
        )
    }
}

@Composable
@Preview
private fun PreviewAction() {
    RumbleTheme {
        PlayerActionButton(
            backgroundColor = rumbleGreen,
            text = "Action Name",
            textColor = enforcedDarkmo,
            uiType = UiType.DISCOVER,
            action = {}
        )
    }
}