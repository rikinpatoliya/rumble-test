package com.rumble.battles.commonViews.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.commentActionButtonWidth

@Composable
fun DeleteWatchHistoryConfirmationAlertDialog(
    onCancel: () -> Unit,
    onDelete: () -> Unit,
) {
    RumbleAlertDialog(
        onDismissRequest = { },
        title = stringResource(id = R.string.delete_watch_history),
        text = stringResource(id = R.string.delete_watch_history_alert_description),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(R.string.cancel),
                dialogActionType = DialogActionType.Neutral,
                withSpacer = true,
                width = commentActionButtonWidth,
                action = { onCancel() }
            ),
            DialogActionItem(
                text = stringResource(R.string.delete_history),
                dialogActionType = DialogActionType.Destructive,
                width = commentActionButtonWidth,
                action = { onDelete() }
            )
        )
    )
}