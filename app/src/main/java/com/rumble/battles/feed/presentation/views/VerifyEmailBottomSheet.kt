package com.rumble.battles.feed.presentation.views

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomSheetItem
import com.rumble.battles.commonViews.RumbleBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VerifyEmailBottomSheet(
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState,
    subtitle: String,
    onRequestLink: () -> Unit,
    onCheckVerificationStatus: () -> Unit,
) {
    RumbleBottomSheet(
        title = stringResource(id = R.string.email_verification_required),
        subtitle = subtitle,
        sheetItems = listOf(
            BottomSheetItem(
                imageResource = R.drawable.ic_email,
                text = stringResource(id = R.string.resend_verification_link)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    onRequestLink()
                }
            },
            BottomSheetItem(
                imageResource = R.drawable.ic_replay,
                text = stringResource(id = R.string.check_your_verification_status)
            ) {
                coroutineScope.launch {
                    bottomSheetState.hide()
                    onCheckVerificationStatus()
                }
            },
        ),
        onCancel = { coroutineScope.launch { bottomSheetState.hide() } }
    )
}