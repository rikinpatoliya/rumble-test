package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingXXXSmall

@Composable
internal fun DurationView(
    modifier: Modifier = Modifier,
    currentPosition: Long = 0,
    totalDuration: Long = 0
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentPosition.parsedTime(),
            color = enforcedWhite,
            style = RumbleTypography.h6Bold
        )
        Text(
            modifier = Modifier.padding(horizontal = paddingXXXSmall),
            text = "/",
            color = enforcedWhite.copy(alpha = 0.7f),
            style = RumbleTypography.h6Bold
        )
        Text(
            text = totalDuration.parsedTime(),
            color = enforcedWhite.copy(alpha = 0.7f),
            style = RumbleTypography.h6Bold
        )
    }
}

internal fun Long.parsedTime(): String {
    val thisInSeconds = this / 1000
    val hours = (thisInSeconds / 60 / 60).toInt()
    val minutes = ((thisInSeconds - (hours * 60 * 60)) / 60).toInt()
    val seconds = (thisInSeconds - (hours * 60 * 60) - (minutes * 60)).toInt()
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else if (minutes > 0) {
        String.format("%02d:%02d", minutes, seconds)
    } else {
        String.format("0:%02d", seconds)
    }
}