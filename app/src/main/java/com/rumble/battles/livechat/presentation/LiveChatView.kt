package com.rumble.battles.livechat.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.feed.presentation.videodetails.VideoDetailsHandler
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.livechat.presentation.content.LiveChatViewContent
import com.rumble.theme.commentActionButtonWidth
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LiveChatView(
    modifier: Modifier = Modifier,
    handler: VideoDetailsHandler,
    liveChatHandler: LiveChatHandler,
    activityHandler: RumbleActivityHandler,
    onChannelClick: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        liveChatHandler.eventFlow.collectLatest {
            when (it) {
                is LiveChatEvent.RantPurchaseSucceeded -> {
                    handler.onRantPurchaseSucceeded(it.rantLevel)
                }

                is LiveChatEvent.OpenModerationMenu -> {
                    handler.onOpenModerationMenu()
                }

                is LiveChatEvent.HideModerationMenu -> {
                    handler.onDismissBottomSheet()
                    handler.onDismissMuteMenu()
                }

                is LiveChatEvent.Error -> {
                    handler.onError(it.errorMessage)
                }

                else -> return@collectLatest
            }
        }
    }

    LiveChatViewContent(
        modifier = modifier,
        handler = handler,
        liveChatHandler = liveChatHandler,
        activityHandler = activityHandler,
        onChannelClick = onChannelClick,
    )

    LiveChatDialog(handler = handler, liveChatHandler = liveChatHandler)
}

@Composable
private fun LiveChatDialog(
    handler: VideoDetailsHandler,
    liveChatHandler: LiveChatHandler,
) {
    val state by handler.state
    val alertDialogState by liveChatHandler.alertDialogState

    if (alertDialogState.show) {
        when (val reason = alertDialogState.alertDialogReason) {
            LiveChatAlertReason.UnpinMessage -> {
                RumbleAlertDialog(
                    onDismissRequest = liveChatHandler::onDismiss,
                    title = stringResource(id = R.string.unpin_message),
                    text = stringResource(id = R.string.unpin_message_confirmation),
                    actionItems = listOf(
                        DialogActionItem(
                            text = stringResource(R.string.cancel),
                            width = commentActionButtonWidth,
                            withSpacer = true,
                            action = liveChatHandler::onDismiss,
                            dialogActionType = DialogActionType.Neutral,
                        ),
                        DialogActionItem(
                            text = stringResource(R.string.unpin),
                            dialogActionType = DialogActionType.Destructive,
                            width = commentActionButtonWidth,
                            action = {
                                state.videoEntity?.id?.let {
                                    liveChatHandler.onConfirmUnpinMessage(it)
                                }
                            },
                        )
                    )
                )
            }

            LiveChatAlertReason.DeleteMessage -> {
                RumbleAlertDialog(
                    onDismissRequest = liveChatHandler::onDismiss,
                    title = stringResource(id = R.string.delete_chat_message),
                    text = stringResource(id = R.string.delete_chat_message_confirmation),
                    actionItems = listOf(
                        DialogActionItem(
                            text = stringResource(R.string.cancel),
                            width = commentActionButtonWidth,
                            withSpacer = true,
                            action = liveChatHandler::onDismiss,
                            dialogActionType = DialogActionType.Neutral,
                        ),
                        DialogActionItem(
                            text = stringResource(R.string.delete),
                            dialogActionType = DialogActionType.Destructive,
                            width = commentActionButtonWidth,
                            action = liveChatHandler::onConfirmDeleteMessage,
                        )
                    )
                )
            }

            is LiveChatAlertReason.ErrorMessage -> {
                RumbleAlertDialog(
                    onDismissRequest = liveChatHandler::onDismiss,
                    text = reason.errorMessage,
                    actionItems = listOf(
                        DialogActionItem(
                            text = stringResource(R.string.ok),
                            width = commentActionButtonWidth,
                            action = liveChatHandler::onDismiss,
                            dialogActionType = DialogActionType.Neutral,
                        )
                    )
                )
            }
        }
    }
}
