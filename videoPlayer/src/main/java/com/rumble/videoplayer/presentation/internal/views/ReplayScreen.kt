package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.rumble.theme.brandedPlayerBackground

@Composable
internal fun ReplayScreen(modifier: Modifier, thumbnail: String?) {

    Box(modifier = modifier) {
        if (thumbnail.isNullOrBlank().not()) {
            AsyncImage(
                modifier = Modifier
                    .background(brandedPlayerBackground)
                    .fillMaxSize(),
                model = thumbnail,
                contentDescription = "",
                contentScale = ContentScale.Fit
            )

            Box(
                modifier = Modifier
                    .background(brandedPlayerBackground.copy(0.6f))
                    .fillMaxSize()
            )
        }
    }
}
