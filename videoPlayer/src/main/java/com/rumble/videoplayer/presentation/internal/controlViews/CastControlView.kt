package com.rumble.videoplayer.presentation.internal.controlViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.paddingMedium
import com.rumble.videoplayer.player.RumblePlayer
import com.rumble.videoplayer.presentation.views.CastView

@Composable
internal fun CastControlView(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer
) {
    Box(modifier = modifier) {
        if (rumblePlayer.videoThumbnailUri.isBlank().not()) {
            AsyncImage(
                modifier = Modifier
                    .background(brandedPlayerBackground)
                    .fillMaxSize(),
                model = rumblePlayer.videoThumbnailUri,
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )

            Box(
                modifier = Modifier
                    .background(brandedPlayerBackground.copy(0.6f))
                    .fillMaxSize()
            )
        }

        CastView(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(paddingMedium)
        )
    }
}