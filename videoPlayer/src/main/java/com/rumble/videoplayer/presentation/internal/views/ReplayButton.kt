package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.rumbleGreen
import com.rumble.videoplayer.R
import com.rumble.videoplayer.presentation.UiType

@Composable
@Preview
internal fun ReplayButton(
    modifier: Modifier = Modifier,
    uiType: UiType = UiType.EMBEDDED,
    isFocused: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(defineBackgroundColor(uiType, isFocused))
            .clickable { onClick() }) {

        if (uiType == UiType.TV && isFocused) {
            Icon(
                modifier = Modifier.padding(
                    top = paddingLarge,
                    bottom = paddingLarge,
                    start = paddingXLarge,
                    end = paddingXLarge
                ),
                painter = painterResource(id = R.drawable.ic_tv_replay),
                contentDescription = stringResource(id = R.string.replay),
                tint = enforcedDarkmo
            )
        } else if (uiType == UiType.TV) {
            Icon(
                modifier = Modifier.padding(
                    top = paddingLarge,
                    bottom = paddingLarge,
                    start = paddingXLarge,
                    end = paddingXLarge
                ),
                painter = painterResource(id = R.drawable.ic_tv_replay),
                contentDescription = stringResource(id = R.string.replay),
                tint = enforcedWhite
            )
        } else {
            Icon(
                modifier = Modifier.padding(
                    top = paddingSmall,
                    bottom = paddingSmall,
                    start = paddingMedium,
                    end = paddingMedium
                ),
                painter = painterResource(id = R.drawable.ic_replay_button),
                contentDescription = stringResource(id = R.string.replay),
                tint = enforcedWhite
            )
        }
    }
}

private fun defineBackgroundColor(uiType: UiType, isFocused: Boolean) =
    if (uiType == UiType.TV && isFocused)
        rumbleGreen
    else if (uiType == UiType.TV)
        enforcedWhite.copy(alpha = 0.2f)
    else
        enforcedCloud.copy(0.4f)
