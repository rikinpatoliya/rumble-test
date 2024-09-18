package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import com.rumble.battles.LoadingTag
import com.rumble.theme.indicatorWidth
import com.rumble.theme.progressIndicatorSize
import com.rumble.theme.rumbleGreen

@Composable
fun RumbleProgressIndicator(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier
            .semantics { testTag = LoadingTag }
            .size(progressIndicatorSize),
        color = rumbleGreen,
        strokeWidth = indicatorWidth
    )
}