package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rumble.theme.brandedBufferDark
import com.rumble.theme.brandedBufferLight
import com.rumble.videoplayer.presentation.internal.defaults.bufferingStrokeWidth
import com.rumble.videoplayer.presentation.internal.defaults.embeddedSeekBarHeight

@Composable
internal fun BufferingView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(createStripeBrush(brandedBufferDark, brandedBufferLight, bufferingStrokeWidth))
            .fillMaxSize()
    )
}

@Composable
private fun createStripeBrush(
    stripeColor: Color,
    stripeBg: Color,
    stripeWidth: Dp
): Brush {
    val stripeWidthPx = with(LocalDensity.current) { stripeWidth.toPx() }
    val brushSizePx = 2 * stripeWidthPx
    val infiniteTransition = rememberInfiniteTransition()
    val stripeStart = stripeWidthPx / brushSizePx
    val offsetStart by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = stripeWidth.value,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 4000
                0.0f at 0
                stripeWidthPx * 100.0f at 4000
            },
            repeatMode = RepeatMode.Restart
        )
    )

    return Brush.linearGradient(
        stripeStart to stripeBg,
        stripeStart to stripeColor,
        start = Offset(offsetStart, 0f),
        end = Offset(offsetStart + brushSizePx, brushSizePx),
        tileMode = TileMode.Repeated
    )
}

@Composable
@Preview
private fun Preview() {
    BufferingView(modifier = Modifier
        .fillMaxWidth()
        .height(embeddedSeekBarHeight))
}