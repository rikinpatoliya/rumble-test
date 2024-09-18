package com.rumble.battles.commonViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.rumble.theme.discoverPlayerSwipeVelocityThreshold
import kotlinx.coroutines.launch


enum class SwipeDirection(val value: Int) {
    Initial(0),
    Left(1),
    Right(2),
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableLikeDislikeView(
    modifier: Modifier = Modifier,
    swipeableState: SwipeableState<SwipeDirection>,
    swipePx: Float,
    iconSizeOffset: Float,
    onLike: () -> Unit,
    onDisLike: () -> Unit,
    onClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val anchors = mapOf(
        0f to SwipeDirection.Initial,
        swipePx to SwipeDirection.Right,
        -swipePx - iconSizeOffset to SwipeDirection.Left
    )

    if (swipeableState.isAnimationRunning) {
        DisposableEffect(Unit) {
            onDispose {
                when (swipeableState.currentValue) {
                    SwipeDirection.Right -> onLike()
                    SwipeDirection.Left -> onDisLike()
                    else -> return@onDispose
                }
                scope.launch { swipeableState.snapTo(SwipeDirection.Initial) }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(1f) },
                orientation = Orientation.Horizontal,
                velocityThreshold = discoverPlayerSwipeVelocityThreshold
            )
            .clickable { onClick() }
    ) {
    }
}