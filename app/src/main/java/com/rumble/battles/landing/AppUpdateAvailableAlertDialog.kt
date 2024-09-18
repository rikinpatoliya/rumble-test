package com.rumble.battles.landing

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog

@Composable
fun AppUpdateAvailableAlertDialog(
    onDismiss: () -> Unit,
    onGoToStore: () -> Unit,
) {
    RumbleAlertDialog(
        onDismissRequest = onDismiss,
        title = stringResource(id = R.string.new_app_update_available),
        text = stringResource(id = R.string.new_app_update_available_description),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(id = R.string.maybe_later),
                action = onDismiss,
                dialogActionType = DialogActionType.Neutral,
                withSpacer = true
            ),
            DialogActionItem(
                text = stringResource(id = R.string.go_to_store),
                action = onGoToStore,
                dialogActionType = DialogActionType.Positive
            ),
        )
    )
}