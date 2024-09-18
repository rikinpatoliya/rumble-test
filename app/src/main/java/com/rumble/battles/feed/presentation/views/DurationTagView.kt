package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.RumbleTypography.tinyBodySemiBold
import com.rumble.theme.RumbleTypography.tinyBodySemiBold8dp
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXSmall
import com.rumble.utils.extension.parsedTime

@Composable
fun DurationTagView(
    modifier: Modifier = Modifier,
    duration: Long,
    listToggleViewStyle: ListToggleViewStyle
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusXSmall))
            .background(enforcedDarkmo)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = paddingXSmall, vertical = paddingXXXSmall),
            text = duration.parsedTime(),
            style = if (listToggleViewStyle == ListToggleViewStyle.GRID) tinyBodySemiBold else tinyBodySemiBold8dp,
            color = enforcedWhite
        )
    }
}