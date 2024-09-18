package com.rumble.battles.bottomSheets

import androidx.compose.runtime.Composable
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.commonViews.RumbleBottomSheet

@Composable
fun MoreUploadOptionsBottomSheet(
    title: String? = null,
    subtitle: String? = null,
    bottomSheetItems: List<BottomSheetItem>,
    onHideBottomSheet: () -> Unit
) {
    RumbleBottomSheet(
        title = title,
        subtitle = subtitle,
        sheetItems = bottomSheetItems,
        onCancel = onHideBottomSheet
    )
}