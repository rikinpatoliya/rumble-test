package com.rumble.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusLarge
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.conditional

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    leadingIconPainter: Painter? = null,
    trailingIconPainter: Painter? = null,
    contentModifier: Modifier = Modifier,
    textModifier: Modifier = Modifier
        .padding(
            horizontal = paddingMedium,
            vertical = paddingXSmall
        ),
    backgroundColor: Color = rumbleGreen,
    borderColor: Color = rumbleGreen,
    textColor: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = RumbleTypography.h6,
    showBorder: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val currentBackgroundColor =
        if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.5f)
    val currentBorderColor = if (enabled) borderColor else borderColor.copy(alpha = 0.5f)
    val currentTextColor = if (enabled) textColor else textColor.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .width(IntrinsicSize.Max)
            .clip(RoundedCornerShape(radiusLarge))
            .conditional(showBorder) {
                border(
                    borderXXSmall,
                    color = currentBorderColor,
                    shape = RoundedCornerShape(radiusLarge)
                )
            }
            .background(color = currentBackgroundColor)
            .conditional(enabled) { clickable { onClick.invoke() } }
    ) {
        Row(
            modifier = contentModifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            leadingIconPainter?.let {
                Icon(
                    painter = leadingIconPainter,
                    contentDescription = text,
                    tint = currentTextColor
                )
            }
            Text(
                modifier = textModifier,
                text = text,
                style = textStyle,
                color = currentTextColor,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            trailingIconPainter?.let {
                Icon(
                    painter = trailingIconPainter,
                    contentDescription = text,
                    tint = currentTextColor
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewActionButton() {
    ActionButton(
        text = stringResource(id = R.string.refresh),
    )
}

@Composable
@Preview
fun PreviewActionButtonDisabled() {
    ActionButton(
        text = stringResource(id = R.string.refresh),
        enabled = false
    )
}