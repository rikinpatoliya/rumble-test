package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.*
import com.rumble.theme.RumbleTypography.h4SemiBold


@Composable
fun PillView(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    text: String,
    color: Color = MaterialTheme.colors.onSecondary,
    textColor: Color = MaterialTheme.colors.primary,
    selectedColor: Color = MaterialTheme.colors.primary,
    selectedTextColor: Color = MaterialTheme.colors.onPrimary,
    live: Boolean = false
) {

    val _textColor = if (selected) {
        selectedTextColor
    } else {
        textColor
    }

    Box(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
            .background(
                color = if (selected) {
                    selectedColor
                } else {
                    color
                }, shape = CircleShape
            )
    ) {
        Row(
            modifier = Modifier
                .padding(
                    horizontal = paddingMedium,
                    vertical = paddingXSmall
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (live) {
                Icon(
                    modifier = Modifier.padding(end = paddingXXXSmall),
                    painter = painterResource(id = R.drawable.ic_live_circle),
                    contentDescription = text,
                    tint = _textColor
                )
            }

            Text(
                modifier = Modifier,
                text = text,
                style = h4SemiBold,
                color = _textColor,
            )

        }
    }

}

@Preview
@Composable
fun PreviewPillView() {
    RumbleTheme {
        Column() {
            val text = "My Feed"
            PillView(text = text)
            PillView(text = text, selected = true)
            PillView(
                text = text,
                color = fierceRed,
                textColor = MaterialTheme.colors.onPrimary,
                live = true
            )
            PillView(
                text = text,
                selected = true,
                color = fierceRed,
                textColor = MaterialTheme.colors.onPrimary
            )
        }
    }
}