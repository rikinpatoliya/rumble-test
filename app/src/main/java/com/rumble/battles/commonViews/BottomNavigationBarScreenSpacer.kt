package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.rumble.theme.bottomBarSpacerBehind

@Composable
fun BottomNavigationBarScreenSpacer(spacerHeight: Dp = bottomBarSpacerBehind) {
    Spacer(
        Modifier
            .height(spacerHeight)
    )
}