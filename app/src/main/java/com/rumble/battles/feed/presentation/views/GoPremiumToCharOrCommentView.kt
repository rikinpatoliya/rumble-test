package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.rumble.theme.RumbleTypography
import com.rumble.theme.elevation
import com.rumble.theme.paddingXMedium
import com.rumble.theme.wokeGreen

@Composable
fun GoPremiumToCharOrCommentView(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        elevation = elevation
    ) {
        Text(
            modifier = modifier
                .clickable { onClick() }
                .padding(vertical = paddingXMedium)
                .fillMaxWidth(),
            text = text,
            style = RumbleTypography.h4Underlined,
            color = wokeGreen,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}