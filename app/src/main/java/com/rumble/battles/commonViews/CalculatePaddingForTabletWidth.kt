package com.rumble.battles.commonViews

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.rumble.theme.tabletMaxWidth

@Composable
fun CalculatePaddingForTabletWidth(maxWidth: Dp, defaultPadding: Dp = 0.dp): Dp =
    if (IsTablet()) {
        max((maxWidth - tabletMaxWidth) / 2, defaultPadding)
    } else {
        defaultPadding
    }