package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedWhite
import com.rumble.theme.getPlaceholderColor
import com.rumble.theme.imageMedium

@Composable
internal fun UserNamePlaceholderView(
    modifier: Modifier = Modifier,
    userName: String
) {
    if (userName.isNotBlank()) {
        Box(
            modifier = modifier
                .size(imageMedium)
                .background(getPlaceholderColor(userName)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = userName.first().toString().uppercase(),
                color = enforcedWhite,
                style = h3
            )
        }
    } else {
        Box(modifier = modifier.background(enforcedCloud))
    }
}
