package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.rumble.battles.R
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.borderXXSmall
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall
import com.rumble.theme.royalPurple
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.simpleString
import java.time.LocalDateTime

@Composable
fun PpvTagsView(
    modifier: Modifier = Modifier,
    ppvEntity: PpvEntity,
    textStyle: TextStyle = h6,
    radius: Dp = radiusSmall,
    withFilledBackground: Boolean = false,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(paddingXSmall)
    ) {
        SolidTagView(
            text = stringResource(id = if (ppvEntity.isPurchased) R.string.purchased else R.string.buy_access),
            color = royalPurple,
            textStyle = textStyle,
            radius = radius
        )

        ppvEntity.purchaseDeadline?.let {
            val futureExpireDate = ppvEntity.purchaseDeadline?.isAfter(LocalDateTime.now()) == true
            if (futureExpireDate) {
                OutlineTagView(
                    text = stringResource(id = R.string.expires_in)
                            + ppvEntity.purchaseDeadline?.simpleString(
                        LocalContext.current
                    ),
                    color = MaterialTheme.colors.secondary,
                    backgroundColor = if (withFilledBackground) MaterialTheme.colors.onSurface else null,
                    textStyle = textStyle,
                    radius = radius
                )
            } else {
                OutlineTagView(
                    text = stringResource(id = R.string.expired),
                    color = fierceRed,
                    backgroundColor = if (withFilledBackground) MaterialTheme.colors.onSurface else null,
                    textStyle = textStyle,
                    radius = radius
                )
            }
        }
    }
}

@Composable
private fun SolidTagView(
    text: String,
    color: Color,
    textStyle: TextStyle,
    radius: Dp
) {
    TagView(
        modifier = Modifier
            .clip(RoundedCornerShape(radius))
            .background(color),
        textColor = enforcedWhite,
        text = text,
        textStyle = textStyle
    )
}

@Composable
private fun OutlineTagView(
    text: String,
    color: Color,
    backgroundColor: Color? = null,
    textStyle: TextStyle,
    radius: Dp,
) {
    TagView(
        modifier = Modifier
            .clip(RoundedCornerShape(radiusSmall))
            .conditional(backgroundColor != null) {
                this.background(color = backgroundColor!!)
            }
            .border(
                width = borderXXSmall,
                shape = RoundedCornerShape(radius),
                color = color
            ),
        textColor = color,
        text = text,
        textStyle = textStyle
    )
}

@Composable
private fun TagView(
    modifier: Modifier = Modifier,
    textColor: Color,
    text: String,
    textStyle: TextStyle
) {
    Box(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .padding(
                    vertical = paddingXXXSmall,
                    horizontal = paddingXSmall
                ),
            text = text,
            style = textStyle,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}