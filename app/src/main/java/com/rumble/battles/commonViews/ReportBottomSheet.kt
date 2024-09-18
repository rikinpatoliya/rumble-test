package com.rumble.battles.commonViews

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.videoplayer.player.config.ReportType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReportBottomSheet(
    subtitle: String,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    onReport: (reason: ReportType) -> Unit,
) {
    RumbleBottomSheet(
        title = stringResource(id = R.string.report),
        subtitle = subtitle,
        sheetItems = listOf(
            BottomSheetItem(
                text = stringResource(id = ReportType.SPAM.value)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    onReport(ReportType.SPAM)
                }
            },
            BottomSheetItem(
                text = stringResource(id = ReportType.INAPPROPRIATE.value)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    onReport(ReportType.INAPPROPRIATE)
                }
            },
            BottomSheetItem(
                text = stringResource(id = ReportType.COPYRIGHT.value)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    onReport(ReportType.COPYRIGHT)
                }
            },
            BottomSheetItem(
                text = stringResource(id = ReportType.TERMS.value)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    onReport(ReportType.TERMS)
                }
            },
        ),
        onCancel = { coroutineScope.launch { bottomSheetState.hide() } }
    )
}