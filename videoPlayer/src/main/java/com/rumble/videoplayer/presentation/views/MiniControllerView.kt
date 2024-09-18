package com.rumble.videoplayer.presentation.views

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.rumble.videoplayer.databinding.ViewMiniControllerBinding

@Composable
fun MiniControllerView(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        AndroidViewBinding(ViewMiniControllerBinding::inflate) {
            miniControllerContainer
        }
    }
}