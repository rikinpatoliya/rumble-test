package com.rumble.battles.commonViews.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTheme
import com.rumble.theme.commentActionButtonWidth

@Composable
fun PurchaseConfirmationDialog(
    onDismiss: () -> Unit
) {
    RumbleAlertDialog(
        onDismissRequest = onDismiss,
        title = stringResource(id = R.string.you_all_set),
        text = stringResource(id = R.string.purchase_successful),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(R.string.ok),
                dialogActionType = DialogActionType.Neutral,
                withSpacer = true,
                width = commentActionButtonWidth,
                action = onDismiss
            )
        )
    )
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        PurchaseConfirmationDialog {

        }
    }
}