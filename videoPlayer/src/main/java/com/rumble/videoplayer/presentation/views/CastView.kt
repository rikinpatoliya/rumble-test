package com.rumble.videoplayer.presentation.views

import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import com.rumble.videoplayer.R

@Composable
fun CastView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mediaButton = MediaRouteButton(context).apply {
        setRemoteIndicatorDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_cast))
    }
    CastButtonFactory.setUpMediaRouteButton(context, mediaButton)

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.align(Alignment.Center),
            factory = {
                mediaButton
            })
    }
}