package com.rumble.battles.commonViews

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXXSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusLarge

@Composable
fun TitleWithBoxedCount(
    title: String = "",
    count: String,
) {
    if (title.isNotEmpty()) {
        Text(
            modifier = Modifier
                .padding(end = paddingXXXSmall),
            text = title.uppercase(),
            style = RumbleTypography.h6Heavy
        )
    }
    Box(
        modifier = Modifier
            .padding(start = paddingXXXSmall)
            .clip(RoundedCornerShape(radiusLarge))
            .border(
                borderXXSmall,
                color = MaterialTheme.colors.secondaryVariant,
                shape = RoundedCornerShape(radiusLarge)
            )
    ) {
        Text(
            modifier = Modifier.padding(
                start = paddingXSmall,
                top = paddingXXXSmall,
                end = paddingXSmall,
                bottom = paddingXXXSmall
            ),
            text = count,
            style = RumbleTypography.h6,
            color = MaterialTheme.colors.primary
        )
    }
}