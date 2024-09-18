package com.rumble.battles.bottomSheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.commonViews.RumbleBottomSheet
import com.rumble.domain.settings.domain.domainmodel.ColorMode

@Composable
fun ChangeAppearanceBottomSheet(
    onUpdateColorMode: (colorMode: ColorMode) -> Unit,
    onHideBottomSheet: () -> Unit
) {
    RumbleBottomSheet(
        title = stringResource(id = R.string.change_appearance),
        sheetItems = listOf(
            BottomSheetItem(
                imageResource = R.drawable.ic_light_mode,
                text = stringResource(id = R.string.light_mode)
            ) {
                onHideBottomSheet()
                onUpdateColorMode(ColorMode.LIGHT_MODE)
            },
            BottomSheetItem(
                imageResource = R.drawable.ic_dark_mode,
                text = stringResource(id = R.string.dark_mode)
            ) {
                onHideBottomSheet()
                onUpdateColorMode(ColorMode.DARK_MODE)
            },
            BottomSheetItem(
                imageResource = R.drawable.ic_auto_mode,
                text = stringResource(id = R.string.system_default)
            ) {
                onHideBottomSheet()
                onUpdateColorMode(ColorMode.SYSTEM_DEFAULT)
            }
        ),
        onCancel = onHideBottomSheet
    )
}