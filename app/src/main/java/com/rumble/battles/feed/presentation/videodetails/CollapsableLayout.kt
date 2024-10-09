package com.rumble.battles.feed.presentation.videodetails

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rumble.theme.elevation
import com.rumble.theme.radiusNone
import com.rumble.utils.RumbleConstants.COLLAPSE_ANIMATION_DURATION
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CollapseDirection {
    UP,
    DOWN
}

enum class CollapsableLayoutState {
    NONE,
    COLLAPSED,
    EXPENDED
}

private const val autoScrollDownPercentage = 0.2f
private const val autoScrollUpPercentage = 0.9f

@Composable
fun CollapsableLayout(
    modifier: Modifier = Modifier,
    collapseAvailable: Boolean = true,
    enforcedState: CollapsableLayoutState = CollapsableLayoutState.EXPENDED,
    shadowElevation: Dp = elevation,
    bottomThreshold: Dp = 0.dp,
    cornerRadius: Dp = radiusNone,
    delayBeforeExpend: Long = 0L,
    onCollapseProgress: (Float, CollapseDirection) -> Unit = { _, _ -> },
    onStateChanged: (CollapsableLayoutState) -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    var containerHeight by remember { mutableFloatStateOf(0f) }
    var maxOffset by remember { mutableFloatStateOf(0f) }
    val sheetOffset = remember { Animatable(0f) }
    var collapseDirection by remember { mutableStateOf(CollapseDirection.DOWN) }
    val bottomPaddingPx: Float = with(LocalDensity.current) { bottomThreshold.toPx() }
    var currentSate by rememberSaveable { mutableStateOf(enforcedState) }
    val currentEnforcedState by rememberUpdatedState(enforcedState)
    val collapseAvailableCurrent by rememberUpdatedState(collapseAvailable)

    LaunchedEffect(maxOffset) {
        if (maxOffset > 0 && enforcedState == CollapsableLayoutState.COLLAPSED) {
            sheetOffset.snapTo(maxOffset)
            currentSate = CollapsableLayoutState.COLLAPSED
        }
    }

    LaunchedEffect(currentEnforcedState) {
        if (currentEnforcedState != CollapsableLayoutState.NONE) {
            collapseDirection =
                if (currentEnforcedState == CollapsableLayoutState.COLLAPSED) CollapseDirection.DOWN
                else CollapseDirection.UP

            onCollapseProgress(sheetOffset.value / maxOffset, collapseDirection)

            if (currentEnforcedState == CollapsableLayoutState.COLLAPSED) {
                sheetOffset.animateTo(
                    targetValue = maxOffset,
                    animationSpec = tween(COLLAPSE_ANIMATION_DURATION),
                    block = {
                        if (sheetOffset.value == maxOffset)
                            currentSate = CollapsableLayoutState.COLLAPSED
                    }
                )
            } else {
                delay(delayBeforeExpend)
                sheetOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(COLLAPSE_ANIMATION_DURATION),
                    block = {
                        if (sheetOffset.value == maxOffset) currentSate =
                            CollapsableLayoutState.EXPENDED
                    }
                )
            }
        }
    }

    LaunchedEffect(sheetOffset.value) {
        val percentage = sheetOffset.value / maxOffset
        onCollapseProgress(percentage, collapseDirection)

        if (percentage >= 1f) {
            currentSate = CollapsableLayoutState.COLLAPSED
            onStateChanged(currentSate)
        } else if (percentage <= 0f) {
            currentSate = CollapsableLayoutState.EXPENDED
            onStateChanged(currentSate)
        }
    }

    LaunchedEffect(collapseDirection) {
        if (collapseAvailable.not()) {
            onCollapseProgress(0f, collapseDirection)
        }
    }

    Surface(
        modifier = modifier
            .padding(top = (sheetOffset.value / LocalDensity.current.density).dp)
            .onGloballyPositioned { coordinates ->
                if (maxOffset == 0f) {
                    containerHeight = coordinates.size.height.toFloat()
                    maxOffset = containerHeight - bottomPaddingPx
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (collapseAvailableCurrent) {
                                if ((sheetOffset.value < containerHeight * autoScrollDownPercentage && collapseDirection == CollapseDirection.DOWN) ||
                                    (sheetOffset.value < containerHeight * autoScrollUpPercentage && collapseDirection == CollapseDirection.UP)
                                ) {
                                    sheetOffset.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(COLLAPSE_ANIMATION_DURATION)
                                    )
                                } else {
                                    sheetOffset.animateTo(
                                        targetValue = maxOffset,
                                        animationSpec = tween(COLLAPSE_ANIMATION_DURATION)
                                    )
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        scope.launch {
                            if (collapseAvailableCurrent) {
                                val newOffset = sheetOffset.value + dragAmount.y
                                sheetOffset.snapTo(newOffset.coerceIn(0f, maxOffset))
                            }
                            collapseDirection =
                                if (change.previousPosition.y < change.position.y) CollapseDirection.DOWN
                                else CollapseDirection.UP
                        }
                        if (sheetOffset.value < maxOffset) change.consume()
                    }
                )
            },
        shadowElevation = shadowElevation,
        shape = RoundedCornerShape(cornerRadius),
        color = Color.Transparent,
        content = content
    )
}