package com.rumble.battles.commonViews

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.rumble.theme.blurRadius
import com.rumble.theme.blurredImage

@Composable
fun BlurredImage(
    modifier: Modifier,
    url: String
) {

    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
        AsyncImage(
            modifier = modifier.blur(blurRadius),
            model = url,
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
    } else {
        Box(modifier) {
            val imageRequest = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .size(blurredImage)
                .diskCachePolicy(CachePolicy.DISABLED)
                .memoryCachePolicy(CachePolicy.DISABLED)
                .build()

            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = imageRequest,
                contentDescription = "",
                contentScale = ContentScale.FillBounds
            )
        }
    }
}