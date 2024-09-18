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
import com.rumble.videoplayer.R
import com.rumble.videoplayer.presentation.UiType

@Composable
fun AdCountDownView(
    modifier: Modifier = Modifier,
    uiType: UiType = UiType.EMBEDDED,
    countDownValue: Int,
) {
    val background =
        if (uiType == UiType.TV) enforcedGray900 else enforcedFiardHighlight.copy(alpha = 0.9f)
    val style = if (uiType == UiType.TV) h4 else h6
    val horizontalPadding = if (uiType == UiType.TV) paddingLarge else paddingXMedium
    val verticalPadding = if (uiType == UiType.TV) paddingMedium else paddingXSmall

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
                text = stringResource(id = R.string.ad_begins, countDownValue),
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
        AdCountDownView(countDownValue = 4, uiType = UiType.EMBEDDED)
    }
}

@Composable
@Preview
private fun PreviewTv() {
    RumbleTheme {
        AdCountDownView(countDownValue = 4, uiType = UiType.TV)
    }
}