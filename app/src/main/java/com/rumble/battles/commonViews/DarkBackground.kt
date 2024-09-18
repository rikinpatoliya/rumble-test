package com.rumble.battles.commonViews

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import com.rumble.battles.R
import com.rumble.theme.enforcedBlack

@Composable
fun DarkModeBackground(modifier: Modifier = Modifier) {

    val configuration = LocalConfiguration.current

    Box(
        modifier = Modifier.background(enforcedBlack)
    ) {
        Image(
            painter = painterResource(id = R.drawable.rumble_pattern_general),
            contentDescription = null,
            modifier = modifier,
            contentScale = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) ContentScale.FillHeight else ContentScale.FillWidth,
            alpha = 0.1f
        )
    }
}