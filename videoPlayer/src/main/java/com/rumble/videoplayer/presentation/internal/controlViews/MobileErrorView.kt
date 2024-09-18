package com.rumble.videoplayer.presentation.internal.controlViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.rumble.theme.RumbleTypography
import com.rumble.theme.brandedPlayerBackground
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingMedium
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumbleVideo
import com.rumble.videoplayer.presentation.UiType

@Composable
fun MobileErrorView(
    modifier: Modifier = Modifier,
    rumbleVideo: RumbleVideo,
    uiType: UiType,
    onBack: () -> Unit
) {
    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier
                .background(brandedPlayerBackground)
                .fillMaxSize(),
            model = rumbleVideo.videoThumbnailUri,
            contentDescription = "",
            contentScale = ContentScale.Fit
        )

        Box(
            modifier = Modifier
                .background(brandedPlayerBackground.copy(0.6f))
                .fillMaxSize()
        )

        if (uiType != UiType.DISCOVER && uiType != UiType.IN_LIST) {
            IconButton(
                modifier = Modifier.align(Alignment.TopStart),
                onClick = onBack
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(id = R.string.back),
                    tint = enforcedWhite
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(paddingMedium)
                .align(Alignment.BottomStart),
            text = stringResource(id = R.string.general_error_message),
            color = enforcedWhite,
            style = RumbleTypography.h6
        )
    }
}

