package com.rumble.battles.livechat.presentation.emoji

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.imageMini
import com.rumble.theme.radiusXSmall

@Composable
fun EmoteTabIndicator(
    modifier: Modifier = Modifier,
    totalCount: Int,
    selection: Int,
) {
    Row(modifier = modifier) {
        repeat(totalCount) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(imageMini)
                    .clip(RoundedCornerShape(radiusXSmall))
                    .background(if (index == selection) RumbleCustomTheme.colors.backgroundHighlight else RumbleCustomTheme.colors.background)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        EmoteTabIndicator(
            modifier = Modifier.width(200.dp),
            totalCount = 5,
            selection = 1,
        )
    }
}