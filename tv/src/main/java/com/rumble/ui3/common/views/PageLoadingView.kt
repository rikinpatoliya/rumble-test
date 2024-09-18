package com.rumble.ui3.common.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rumble.theme.paddingMedium

@Composable
fun PageLoadingView(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        RumbleProgressIndicator(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(paddingMedium)
        )
    }
}