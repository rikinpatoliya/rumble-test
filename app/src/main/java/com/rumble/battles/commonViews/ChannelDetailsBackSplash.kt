package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.rumble.battles.ChannelBackSplashTag
import com.rumble.battles.R
import com.rumble.theme.channelBackSplashHeight
import com.rumble.theme.enforcedDarkmo

@Composable
internal fun ChannelDetailsBaskSplash(
    headerUrl: String?,//Not using currently before backSplash is fixed on backend
) {
    AsyncImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(channelBackSplashHeight)
            .background(color = enforcedDarkmo)
            .testTag(ChannelBackSplashTag),
        model = R.drawable.default_header_image,
        contentDescription = "",
        contentScale = ContentScale.FillBounds
    )
}

@Composable
@Preview
fun PreviewChannelDetailsBaskSplash() {
    ChannelDetailsBaskSplash("")
}