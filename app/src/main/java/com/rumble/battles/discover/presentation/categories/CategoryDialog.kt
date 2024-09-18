package com.rumble.battles.discover.presentation.categories

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.dialogs.AlertDialogReason
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.theme.commentActionButtonWidth

@Composable
fun CategoryDialog(reason: AlertDialogReason, handler: CategoryHandler) {
    when (reason) {
        is CategoryAlertReason.RestrictedContentReason -> {
            RumbleAlertDialog(
                onDismissRequest = { },
                title = stringResource(id = R.string.mature_content),
                text = stringResource(id = R.string.must_be_18),
                testTag = MatureContentPopupTag,
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(R.string.cancel),
                        dialogActionType = DialogActionType.Neutral,
                        withSpacer = true,
                        width = commentActionButtonWidth,
                        action = handler::onCancelRestricted
                    ),
                    DialogActionItem(
                        text = stringResource(R.string.start_watching),
                        dialogActionType = DialogActionType.Positive,
                        width = commentActionButtonWidth,
                        action = { handler.onWatchRestricted(reason.videoEntity, reason.index) }
                    )
                )
            )
        }
    }
}