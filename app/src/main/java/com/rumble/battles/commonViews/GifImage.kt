package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Size

@Composable
fun GifImage(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    val context = LocalContext.current
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(imageUrl).apply(
                block = { size(Size.ORIGINAL) }).build(), imageLoader = context.imageLoader
        ),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
    )
}