package com.rumble.battles.content.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rumble.battles.MatureContentPopupTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.theme.commentActionButtonWidth

@Composable
fun RestrictedContentDialog(
    onCancelRestricted: () -> Unit,
    onWatchRestricted: () -> Unit
) {
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
                action = onCancelRestricted
            ),
            DialogActionItem(
                text = stringResource(R.string.start_watching),
                dialogActionType = DialogActionType.Positive,
                width = commentActionButtonWidth,
                action = onWatchRestricted
            )
        )
    )
}