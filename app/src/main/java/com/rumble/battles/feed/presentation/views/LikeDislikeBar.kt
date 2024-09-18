package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rumble.theme.barCompactHeight
import com.rumble.theme.barDelimiterWidth
import com.rumble.theme.barHeight
import com.rumble.theme.enforcedFiord
import com.rumble.theme.radiusXMedium
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.conditional

@Composable
fun LikeDislikeBar(
    modifier: Modifier = Modifier,
    style: LikeDislikeViewStyle = LikeDislikeViewStyle.Compact,
    likeNumber: Long,
    dislikeNumber: Long
) {
    val height = when (style) {
        LikeDislikeViewStyle.Normal -> barHeight
        else -> barCompactHeight
    }

    Box(
        modifier = modifier.conditional(style != LikeDislikeViewStyle.ActionButtonsWithBarBelow) {
            this.clip(RoundedCornerShape(radiusXMedium))
        }
    ) {
        if (likeNumber > 0 && dislikeNumber > 0) {
            BarWithDelimiter(
                height = height,
                likeWeight = likeNumber.toFloat(),
                dislikeWeight = dislikeNumber.toFloat()
            )
        } else if (likeNumber > 0) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                drawRoundRect(color = rumbleGreen)
            }
        } else if (dislikeNumber > 0) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                drawRoundRect(color = enforcedFiord)
            }
        } else if (likeNumber == 0L && dislikeNumber == 0L && style == LikeDislikeViewStyle.ActionButtonsWithBarBelow) {
            BarWithDelimiter(
                height = height,
                likeWeight = 1F,
                dislikeWeight = 1F
            )
        } else {
            val color = MaterialTheme.colors.secondaryVariant
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height)
            ) {
                drawRoundRect(color = color)
            }
        }
    }
}

@Composable
private fun BarWithDelimiter(
    height: Dp,
    likeWeight: Float,
    dislikeWeight: Float
) {
    Row(
        modifier = Modifier
            .height(height)
            .fillMaxWidth()
    ) {
        Canvas(
            modifier = Modifier
                .weight(likeWeight, true)
                .height(height)
        ) {
            drawRoundRect(color = rumbleGreen)
        }

        Canvas(
            modifier = Modifier
                .width(barDelimiterWidth)
                .height(height)
        ) {
            drawRoundRect(color = Color.Transparent)
        }

        Canvas(
            modifier = Modifier
                .weight(dislikeWeight, true)
                .height(height)
        ) {
            drawRoundRect(color = enforcedFiord)
        }
    }
}

@Composable
@Preview
private fun Preview() {
    LikeDislikeBar(modifier = Modifier, likeNumber = 123, dislikeNumber = 10)
}