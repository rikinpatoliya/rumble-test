package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.RumbleTheme
import com.rumble.theme.imageMini
import com.rumble.theme.imageWidthLarge
import com.rumble.theme.radiusXXSmall

@Composable
fun DrawerCloseIndicatorView(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .width(imageWidthLarge)
                .height(imageMini)
                .clip(RoundedCornerShape(radiusXXSmall))
                .background(MaterialTheme.colors.primaryVariant.copy(alpha = 0.5f))
        )
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        DrawerCloseIndicatorView()
    }
}