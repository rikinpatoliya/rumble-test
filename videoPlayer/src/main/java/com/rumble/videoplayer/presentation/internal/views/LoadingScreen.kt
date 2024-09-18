package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.rumbleGreen
import com.rumble.videoplayer.presentation.UiType
import com.rumble.videoplayer.presentation.internal.defaults.embeddedSeekBarHeight
import com.rumble.videoplayer.presentation.internal.defaults.progressBarSize
import com.rumble.videoplayer.presentation.internal.defaults.progressBarWidth
import com.rumble.videoplayer.presentation.internal.defaults.thumbnailDelay

@Composable
internal fun LoadingScreen(
    modifier: Modifier,
    thumbnail: String?,
    uiType: UiType,
    isVisible: Boolean
) {

    if (uiType == UiType.IN_LIST) {
        AnimatedVisibility(
            visible = isVisible,
            enter = EnterTransition.None,
            exit = fadeOut(
                animationSpec = keyframes {
                    this.durationMillis = thumbnailDelay
                }
            )
        ) {
            InListLoadingScreen(modifier = modifier, thumbnail = thumbnail)
        }
    } else {
        if (isVisible) {
            DefaultLoadingScreen(modifier = modifier, thumbnail = thumbnail)
        }
    }
}

@Composable
private fun InListLoadingScreen(
    modifier: Modifier,
    thumbnail: String?
) {
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
        }

        BufferingView(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(embeddedSeekBarHeight)
        )
    }
}

@Composable
private fun DefaultLoadingScreen(
    modifier: Modifier,
    thumbnail: String?
) {
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
        CircularProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .size(progressBarSize),
            strokeWidth = progressBarWidth,
            color = rumbleGreen
        )
    }
}
