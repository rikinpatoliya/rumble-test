package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.imageMedium
import com.rumble.theme.rumbleGreen

@Composable
fun RoundTextButton(
    modifier: Modifier = Modifier,
    size: Dp = imageMedium,
    text: String = "",
    backgroundColor: Color = rumbleGreen,
    textColor: Color =  MaterialTheme.colors.primary,
    onClick: () -> Unit = {}
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(backgroundColor)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                color = textColor,
                style = h5
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        RoundTextButton(
            text = "500"
        )
    }
}


