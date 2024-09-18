package com.rumble.ui3.common.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.rumble.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedWhite
import com.rumble.theme.radiusMedium

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ErrorView(
    modifier: Modifier,
    backgroundColor: Color = Color.Transparent,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.generic_error_message),
            style = RumbleTypography.h6,
            color = enforcedWhite
        )
    }
}