package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.videoplayer.R
import com.rumble.videoplayer.player.RumblePlayer

@Composable
internal fun SoundButton(
    modifier: Modifier = Modifier,
    rumblePlayer: RumblePlayer
) {
    val isMuted by remember { rumblePlayer.isMuted }

    IconButton(
        onClick = {
            if (isMuted) rumblePlayer.unMute()
            else rumblePlayer.mute()
        },
        modifier = modifier
    ) {
        if (isMuted) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sound_off),
                contentDescription = stringResource(id = R.string.sound_off),
                tint = Color.White
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_sound_on),
                contentDescription = stringResource(id = R.string.sound_on),
                tint = Color.White
            )
        }
    }
}