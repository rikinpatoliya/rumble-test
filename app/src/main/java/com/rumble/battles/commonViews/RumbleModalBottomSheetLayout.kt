package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rumble.theme.enforcedGray950
import com.rumble.theme.noElevation

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RumbleModalBottomSheetLayout(
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = sheetState,
        sheetElevation = noElevation,
        sheetBackgroundColor = Color.Transparent,
        scrimColor = enforcedGray950.copy(alpha = 0.6f),
        sheetContent = sheetContent,
        content = content,
    )
}