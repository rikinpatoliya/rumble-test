package com.rumble.battles.commonViews.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.theme.commentActionButtonWidth

@Composable
fun UnfollowConfirmationAlertDialog(
    onCancelUnfollow: () -> Unit,
    onUnfollow: () -> Unit,
) {
    RumbleAlertDialog(
        onDismissRequest = { },
        title = stringResource(id = R.string.unfollow_creator_title),
        text = stringResource(id = R.string.unfollow_creator_description),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(R.string.cancel),
                dialogActionType = DialogActionType.Neutral,
                withSpacer = true,
                width = commentActionButtonWidth,
                action = { onCancelUnfollow() }
            ),
            DialogActionItem(
                text = stringResource(R.string.unfollow),
                dialogActionType = DialogActionType.Positive,
                width = commentActionButtonWidth,
                action = { onUnfollow() }
            )
        )
    )
}