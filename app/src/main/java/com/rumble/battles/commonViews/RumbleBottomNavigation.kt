package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rumble.theme.bottomBarTabletWidth
import com.rumble.utils.extension.conditional

@Composable
fun RumbleBottomNavigation(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    content: @Composable RowScope.() -> Unit,
) {

    Surface(
        color = backgroundColor,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
        ) {
            Row(
                Modifier
                    .conditional(IsTablet()) { width(bottomBarTabletWidth) }
                    .conditional(!IsTablet()) { fillMaxWidth() }
                    .selectableGroup()
                    .background(backgroundColor)
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                content = content
            )
        }
    }
}