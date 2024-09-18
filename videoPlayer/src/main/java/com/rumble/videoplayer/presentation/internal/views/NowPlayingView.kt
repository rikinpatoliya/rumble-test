package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.rumbleGreen
import com.rumble.videoplayer.presentation.internal.defaults.currentPlayingRadius
import com.rumble.videoplayer.presentation.internal.defaults.currentPlayingSpace
import com.rumble.videoplayer.presentation.internal.defaults.currentPlayingWidth
import com.rumble.videoplayer.presentation.internal.defaults.nowPlayingDuration
import com.rumble.videoplayer.presentation.internal.defaults.nowPlayingDurationShort
import com.rumble.videoplayer.presentation.internal.defaults.nowPlayingOffset
import com.rumble.videoplayer.presentation.internal.defaults.nowPlayingOffsetShort
import com.rumble.videoplayer.presentation.internal.defaults.nowPlayingOffsetShortShrank
import com.rumble.videoplayer.presentation.internal.defaults.nowPlayingOffsetShrank

@Composable
fun NowPlayingView(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(brandedPlayerBackground.copy(alpha = 0.6f))) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(currentPlayingSpace),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NowPlayingLine(nowPlayingOffsetShort, nowPlayingOffsetShortShrank, nowPlayingDurationShort)
            NowPlayingLine(nowPlayingOffset, nowPlayingOffsetShrank, nowPlayingDuration)
            NowPlayingLine(nowPlayingOffsetShort, nowPlayingOffsetShortShrank, nowPlayingDurationShort)
            NowPlayingLine(nowPlayingOffset, nowPlayingOffsetShrank, nowPlayingDuration)
            NowPlayingLine(nowPlayingOffsetShort, nowPlayingOffsetShortShrank, nowPlayingDurationShort)
        }
    }
}

@Composable
private fun NowPlayingLine(offset: Dp, offsetShrank: Dp, duration: Int) {
    var expended by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        expended = true
    }

    val targetOffset: Dp by animateDpAsState(
        targetValue = if (expended) offset else offsetShrank,
        label = "nowPlayingLine",
        animationSpec = tween(duration),
        finishedListener = { expended = expended.not() }
    )
    Box(
        modifier = Modifier
            .width(currentPlayingWidth)
            .fillMaxHeight()
            .padding(vertical = targetOffset)
            .clip(RoundedCornerShape(currentPlayingRadius))
            .background(rumbleGreen)
    )
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    NowPlayingView(modifier = Modifier.size(200.dp))
}