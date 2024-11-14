package com.rumble.utils.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp

fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = composed {
    this.clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

fun Modifier.consumeClick(): Modifier = composed {
    this.clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
    }
}

fun Modifier.ignoreHorizontalParentPadding(horizontal: Dp): Modifier {
    return this.layout { measurable, constraints ->
        val overriddenWidth = constraints.maxWidth + 2 * horizontal.roundToPx()
        val placeable = measurable.measure(constraints.copy(maxWidth = overriddenWidth))
        layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

fun Modifier.dashedBorder(strokeWidth: Dp, color: Color, cornerRadiusDp: Dp) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }

        this.then(
            Modifier.drawWithCache {
                onDrawBehind {
                    val stroke = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    drawRoundRect(
                        color = color,
                        style = stroke,
                        cornerRadius = CornerRadius(cornerRadiusPx)
                    )
                }
            }
        )
    }
)

fun Modifier.onVisible(visibilityPercentage: Float = 1f, onVisible: () -> Unit): Modifier =
    onGloballyPositioned { coordinates ->
        coordinates.parentLayoutCoordinates?.size?.let {
            val globalHeight = coordinates.findRootCoordinates().size.height
            val parentHeight = it.height
            val yInParent = coordinates.positionInParent().y
            val yInWindow = coordinates.positionInWindow().y
            val viewHeight = coordinates.size.height
            if (visibilityPercentage < 1) {
                if ((yInParent + (viewHeight * visibilityPercentage)) <= parentHeight && (yInParent + (viewHeight * (1 - visibilityPercentage))) >= 0) {
                    onVisible()
                }
            } else {
                if ((yInParent + viewHeight) <= parentHeight && yInParent >= 0 && (yInWindow + viewHeight) <= globalHeight && yInWindow >= 0) {
                    onVisible()
                }
            }
        }
    }

fun Modifier.rumbleUitTestTag(testTag: String): Modifier {
    return this.semantics { contentDescription = testTag }
}
