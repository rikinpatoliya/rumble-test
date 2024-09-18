package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.rumble.battles.R
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageMedium
import com.rumble.theme.rumbleGreen

@Composable
fun RoundIconButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    size: Dp = imageMedium,
    contentDescription: String = "",
    backgroundColor: Color = MaterialTheme.colors.background,
    tintColor: Color =  MaterialTheme.colors.primaryVariant,
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