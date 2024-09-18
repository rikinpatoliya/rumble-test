package com.rumble.ui3.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXXXSmall
import com.rumble.utils.extension.parsedTime

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DurationTagView(
    modifier: Modifier = Modifier,
    duration: Long,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusXXXSmall))
            .background(enforcedDarkmo)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = paddingXSmall, vertical = paddingXXXSmall),
            text = duration.parsedTime(),
            style = RumbleTypography.h6Heavy,
            color = enforcedWhite
        )
    }
}

@Composable
@Preview
private fun Preview() {
    DurationTagView(duration = 43)
}