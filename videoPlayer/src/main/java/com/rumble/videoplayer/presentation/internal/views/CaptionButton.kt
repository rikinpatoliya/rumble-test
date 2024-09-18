package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rumble.videoplayer.R

@Composable
internal fun CaptionButton(
    modifier: Modifier = Modifier,
    captionOff: Boolean = false,
    onClick: (Boolean) -> Unit
) {
    var captionOffState by remember { mutableStateOf(captionOff) }

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        onClick = {
            captionOffState = captionOffState.not()
            onClick(captionOffState)
        }) {
        if (captionOffState) {
            Image(
                painter = painterResource(id = R.drawable.ic_captioning_off),
                contentDescription = stringResource(id = R.string.caption_off)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_captioning_on),
                contentDescription = stringResource(id = R.string.caption_on)
            )
        }
    }
}