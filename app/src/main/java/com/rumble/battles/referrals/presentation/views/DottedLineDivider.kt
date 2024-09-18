package com.rumble.battles.referrals.presentation.views

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect

@Composable
fun DottedLineDivider(modifier: Modifier) {
    val color = MaterialTheme.colors.secondaryVariant
    androidx.compose.foundation.Canvas(
        modifier = modifier
    ) {
        drawLine(
            color = color,
            strokeWidth = size.height,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    }
}