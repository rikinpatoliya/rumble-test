package com.rumble.battles.feed.presentation.views

import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.utils.RumbleConstants.LIVE_TIME_UPDATE
import com.rumble.utils.extension.liveDurationString
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun TotalLiveTimeView(
    modifier: Modifier = Modifier,
    startTime: LocalDateTime
) {
    var currentLiveTime by rememberSaveable { mutableLongStateOf(ChronoUnit.MILLIS.between(startTime, LocalDateTime.now())) }
    val context = LocalContext.current

    LaunchedEffect(currentLiveTime) {
        delay(LIVE_TIME_UPDATE)
        currentLiveTime += LIVE_TIME_UPDATE
    }

    Text(
        modifier = modifier,
        text = currentLiveTime.liveDurationString(context),
        style = h6Light,
        color = MaterialTheme.colors.primary
    )
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        TotalLiveTimeView(startTime = LocalDateTime.now())
    }
}