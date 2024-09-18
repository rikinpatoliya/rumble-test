package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.videoplayer.R

@Composable
internal fun FullScreenButton(
    modifier: Modifier = Modifier,
    isFullScreen: Boolean = false,
    onClick: (Boolean) -> Unit = {}
) {
    var fullScreen by remember { mutableStateOf(isFullScreen) }

    IconButton(onClick = {
        fullScreen = fullScreen.not()
        onClick(fullScreen)
    }, modifier = modifier) {
        if (fullScreen) {
            Image(
                painter = painterResource(id = R.drawable.ic_minimize),
                contentDescription = stringResource(id = R.string.minimize)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_fullscreen),
                contentDescription = stringResource(id = R.string.full_screen)
            )
        }
    }
}