package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.enforcedFiardHighlight
import com.rumble.theme.enforcedGray900
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusLarge
import com.rumble.utils.extension.parsedTime
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.config.CountDownType
import com.rumble.videoplayer.presentation.UiType

@Composable
fun CountDownView(
    modifier: Modifier = Modifier,
    uiType: UiType = UiType.EMBEDDED,
    type: CountDownType = CountDownType.Ad,
    countDownValue: Long,
) {
    val background =
        if (uiType == UiType.TV) enforcedGray900 else enforcedFiardHighlight.copy(alpha = 0.9f)
    val style = if (uiType == UiType.TV) h4 else h6
    val horizontalPadding = if (uiType == UiType.TV) paddingLarge else paddingXMedium
    val verticalPadding = if (uiType == UiType.TV) paddingMedium else paddingXSmall
    val text = when (type) {
        CountDownType.Ad -> stringResource(id = R.string.ad_begins, countDownValue)
        CountDownType.Premium -> stringResource(id = R.string.premium_only_in, countDownValue)
        CountDownType.FreePreview -> stringResource(id = R.string.free_preview_end, countDownValue.parsedTime())
    }

    AnimatedVisibility(
        modifier = modifier,
        visible = countDownValue > 0
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(radiusLarge))
                .background(background)
        ) {
            Text(
                modifier = Modifier.padding(
                    vertical = verticalPadding,
                    horizontal = horizontalPadding
                ),
                text = text,
                color = enforcedWhite,
                style = style
            )
        }
    }
}

@Composable
@Preview
private fun PreviewMobile() {
    RumbleTheme {
        CountDownView(countDownValue = 4, uiType = UiType.EMBEDDED)
    }
}

@Composable
@Preview
private fun PreviewTv() {
    RumbleTheme {
        CountDownView(countDownValue = 4, uiType = UiType.TV)
    }
}