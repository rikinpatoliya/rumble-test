package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rumble.theme.RumbleTypography.h6Bold

@Composable
internal fun LiveDurationView(
    modifier: Modifier = Modifier,
    currentPosition: Long = 0,
    totalDuration: Long = 0
) {
    Text(
        modifier = modifier,
        text = getTimeTillLive(currentPosition, totalDuration),
        color = Color.White,
        style = h6Bold
    )
}

private fun getTimeTillLive(currentPosition: Long, totalDuration: Long): String {
    val time = totalDuration - currentPosition
    return if (time > 0) "-${time.parsedTime()}"
    else time.parsedTime()
}
