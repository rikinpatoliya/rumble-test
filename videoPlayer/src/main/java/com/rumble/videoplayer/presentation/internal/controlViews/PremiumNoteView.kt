package com.rumble.videoplayer.presentation.internal.controlViews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXXSmall
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedFiardHighlight
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusMedium
import com.rumble.videoplayer.R

@Composable
fun PremiumNoteView(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .wrapContentSize()
            .background(
                color = enforcedBlack.copy(alpha = 0.8f),
                shape = RoundedCornerShape(radiusMedium)
            )
            .border(
                width = borderXXSmall,
                color = enforcedFiardHighlight,
                shape = RoundedCornerShape(radiusMedium)
            )
    ) {

        Text(
            modifier = Modifier.padding(
                top = paddingMedium,
                start = paddingMedium,
                end = paddingMedium
            ),
            text = stringResource(id = R.string.premium_only_content),
            color = enforcedWhite,
            style = RumbleTypography.tvH3
        )
        Text(
            modifier = Modifier.padding(
                top = paddingXSmall,
                bottom = paddingMedium,
                start = paddingMedium,
                end = paddingMedium
            ),
            text = stringResource(id = R.string.premium_only_content_message),
            color = enforcedWhite,
            style = RumbleTypography.labelRegularTv
        )
    }
}