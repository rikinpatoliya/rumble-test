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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rumble.theme.elevation
import com.rumble.theme.radiusNone
import com.rumble.utils.RumbleConstants.COLLAPSE_ANIMATION_DURATION
import com.rumble.utils.extension.toPx
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class CollapseDirection {
    UP,
    DOWN
}

sealed class CollapsableLayoutState {
    data object None : CollapsableLayoutState()
    data object Collapsed : CollapsableLayoutState()
    data class Expended(val animated: Boolean = true) : CollapsableLayoutState()
}

private const val autoScrollDownPercentage = 0.2f
private const val autoScrollUpPercentage = 0.9f

@Composable
fun CollapsableLayout(
    modifier: Modifier = Modifier,
    collapseAvailable: Boolean = true,
    enforcedState: CollapsableLayoutState = CollapsableLayoutState.Expended(),
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
    var currentSate by remember { mutableStateOf(enforcedState) }
    val currentEnforcedState by rememberUpdatedState(enforcedState)
    val collapseAvailableCurrent by rememberUpdatedState(collapseAvailable)
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    LaunchedEffect(configuration.orientation) {
        containerHeight = (configuration.screenHeightDp.toPx(context)).toFloat()
        maxOffset = containerHeight - bottomPaddingPx
    }

    LaunchedEffect(maxOffset) {
        if (maxOffset > 0 && enforcedState is CollapsableLayoutState.Collapsed) {
            sheetOffset.snapTo(maxOffset)
            currentSate = CollapsableLayoutState.Collapsed
        }
    }

    LaunchedEffect(currentEnforcedState) {
        if (currentEnforcedState != CollapsableLayoutState.None) {
            collapseDirection =
                if (currentEnforcedState == CollapsableLayoutState.Collapsed) CollapseDirection.DOWN
                else CollapseDirection.UP

            onCollapseProgress(sheetOffset.value / maxOffset, collapseDirection)

            when (currentEnforcedState) {
                is CollapsableLayoutState.Collapsed -> {
                    sheetOffset.animateTo(
                        targetValue = maxOffset,
                        animationSpec = tween(COLLAPSE_ANIMATION_DURATION),
                        block = {
                            if (sheetOffset.value == maxOffset)
                                currentSate = CollapsableLayoutState.Collapsed
                        }
                    )
                }

                is CollapsableLayoutState.Expended -> {
                    if ((currentEnforcedState as CollapsableLayoutState.Expended).animated) {
                        delay(delayBeforeExpend)
                        sheetOffset.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(COLLAPSE_ANIMATION_DURATION),
                            block = {
                                if (sheetOffset.value == maxOffset)
                                    currentSate = CollapsableLayoutState.Expended()
                            }
                        )
                    } else {
                        sheetOffset.snapTo(0f)
                        currentSate = CollapsableLayoutState.Expended()
                    }
                }

                is CollapsableLayoutState.None -> return@LaunchedEffect
            }
        }
    }

    LaunchedEffect(sheetOffset.value) {
        val percentage = sheetOffset.value / maxOffset
        onCollapseProgress(percentage, collapseDirection)

        if (percentage >= 1f) {
            currentSate = CollapsableLayoutState.Collapsed
            onStateChanged(currentSate)
        } else if (percentage <= 0f) {
            currentSate = CollapsableLayoutState.Expended()
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
                                if (newOffset >= 0) {
                                    sheetOffset.snapTo(newOffset.coerceIn(0f, maxOffset))
                                }
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