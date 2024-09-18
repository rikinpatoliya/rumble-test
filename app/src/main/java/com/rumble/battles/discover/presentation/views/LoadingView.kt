package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.rumble.theme.radiusMedium

@Composable
fun LoadingView(modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .background(MaterialTheme.colors.onSecondary)
    )
}