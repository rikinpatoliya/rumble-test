package com.rumble.battles.commonViews

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.rumble.theme.rumbleGreen

@Composable
fun RumbleSwipeRefreshIndicator(
    state: SwipeRefreshState,
    refreshTriggerDistance: Dp
) {
    SwipeRefreshIndicator(
        state = state,
        refreshTriggerDistance = refreshTriggerDistance,
        scale = true,
        backgroundColor = MaterialTheme.colors.onSurface,
        contentColor = rumbleGreen
    )
}