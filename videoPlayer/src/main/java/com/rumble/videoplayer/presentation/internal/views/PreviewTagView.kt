package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.tinyBodySemiBold8dp
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusXXSmall
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.config.CountDownType

@Composable
fun PreviewTagView(
    modifier: Modifier = Modifier,
    type: CountDownType,
    countDownValue: Long,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = countDownValue > 0 && type == CountDownType.FreePreview
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(radiusXXSmall))
                .background(enforcedBlack.copy(alpha = 0.9f))
        ) {
            Text(
                modifier = Modifier.padding(vertical = paddingXXXXSmall, horizontal = paddingXXSmall),
                text = stringResource(R.string.preview).uppercase(),
                color = enforcedWhite,
                style = tinyBodySemiBold8dp
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        PreviewTagView(
            type = CountDownType.FreePreview,
            countDownValue = 100
        )
    }
}