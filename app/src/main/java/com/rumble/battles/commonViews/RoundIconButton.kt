package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rumble.battles.R
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageMedium
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants.LONG_PRESS_FREQUENCY
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RoundIconButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    size: Dp = imageMedium,
    contentDescription: String = "",
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colors.background,
    tintColor: Color =  MaterialTheme.colors.primaryVariant,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    var isLongPressing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

    IconButton(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { _ ->
                            isLongPressing = true
                            job = coroutineScope.launch {
                                while (isLongPressing) {
                                    onLongClick()
                                    delay(LONG_PRESS_FREQUENCY)
                                }
                            }
                            tryAwaitRelease()
                            isLongPressing = false
                            job?.cancel()
                        },
                    )
                }
        ) {
            Icon(
                modifier = Modifier.align(Alignment.Center),
                painter = painter,
                contentDescription = contentDescription,
                tint = tintColor
            )
        }
    }
}

@Preview
@Composable
fun PreviewRoundIconButton() {
    RoundIconButton(
        painter = painterResource(id = R.drawable.ic_settings),
        backgroundColor = rumbleGreen,
        tintColor = enforcedWhite,
    )
}

@Preview
@Composable
fun PreviewRoundIconButtonDisabled() {
    RoundIconButton(
        painter = painterResource(id = R.drawable.ic_settings),
        backgroundColor = rumbleGreen,
        tintColor = enforcedWhite,
        enabled = false,
    )
}