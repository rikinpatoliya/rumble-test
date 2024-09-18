package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXMedium


@Composable
fun TagChip(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusXMedium))
            .background(MaterialTheme.colors.onSurface)
            .clickable { onClick() }
    ) {
        Text(
            modifier = Modifier
                .padding(
                    vertical = paddingXXXSmall,
                    horizontal = paddingXSmall
                ),
            text = text,
            style = tinyBody,
            color = MaterialTheme.colors.secondary,
        )
    }
}