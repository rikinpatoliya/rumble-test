package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingSmall
import com.rumble.utils.extension.conditional
import com.rumble.videoplayer.R
import com.rumble.videoplayer.presentation.internal.defaults.addToPlaylistSize

@Composable
fun TvAddToPlaylistButton(
    modifier: Modifier = Modifier,
    isFocused: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (isFocused) {
            Text(
                text = stringResource(id = R.string.tv_add_to_playlist),
                style = RumbleTypography.h6Bold,
                color = enforcedWhite
            )

            Spacer(modifier = Modifier.height(paddingSmall))
        }

        Box(modifier = modifier
            .clip(CircleShape)
            .conditional(isFocused) {
                background(enforcedWhite.copy(alpha = 0.2f))
            }) {
            IconButton(onClick = onClick) {
                Icon(
                    modifier = Modifier.size(addToPlaylistSize),
                    painter = painterResource(id = R.drawable.ic_tv_add_to_playlist),
                    contentDescription = stringResource(id = R.string.tv_add_to_playlist),
                    tint = enforcedWhite
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewUnfocused() {
    TvAddToPlaylistButton(
        isFocused = false,
        onClick = {}
    )
}

@Composable
@Preview
private fun PreviewFocused() {
    TvAddToPlaylistButton(
        isFocused = true,
        onClick = {}
    )
}