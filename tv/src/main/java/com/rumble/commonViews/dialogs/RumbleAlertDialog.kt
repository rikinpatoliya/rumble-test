package com.rumble.commonViews.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.tv.material3.Text
import com.rumble.commonViews.ActionButton
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.channelActionsButtonWidth
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.modalMaxWidth
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.rumbleGreen

interface AlertDialogReason {
    object None : AlertDialogReason
}

data class AlertDialogState(
    val show: Boolean = false,
    val alertDialogReason: AlertDialogReason = AlertDialogReason.None,
)

data class AlertDialogResponseData(
    val success: Boolean,
    val message: String?,
)

enum class DialogActionType {
    Neutral,
    Destructive,
    Positive,
}

data class DialogActionItem(
    val text: String,
    val dialogActionType: DialogActionType = DialogActionType.Neutral,
    val withSpacer: Boolean = false,
    val action: () -> Unit,
    val width: Dp = channelActionsButtonWidth,
)

@Composable
fun RumbleAlertDialog(
    onDismissRequest: () -> Unit,
    title: String? = null,
    text: String? = null,
    actionItems: List<DialogActionItem>,
    annotatedTextWithActions: AnnotatedStringWithActionsList? = null,
    onAnnotatedTextClicked: ((annotatedTextWithActions: AnnotatedStringWithActionsList, offset: Int) -> Unit)? = null,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        val dimAmount = if (RumbleCustomTheme.isLightMode) 0.4f else 0.5f
        // This sets the amount that the rest of the screen behind the dialog darkens
        (LocalView.current.parent as? DialogWindowProvider)?.window?.setDimAmount(dimAmount)

        Surface(
            modifier = Modifier
                .sizeIn(maxWidth = modalMaxWidth),
            shape = RoundedCornerShape(radiusXMedium),
            color = RumbleCustomTheme.colors.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = paddingLarge)
            ) {

                DialogTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = paddingXSmall,
                            start = paddingMedium,
                            end = paddingMedium
                        ),
                    title = title
                )

                DialogTextView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = paddingLarge,
                            start = paddingMedium,
                            end = paddingMedium
                        ),
                    text = text,
                    annotatedTextWithActions = annotatedTextWithActions,
                    onAnnotatedTextClicked = onAnnotatedTextClicked
                )

                DialogButtonsView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = paddingMedium, end = paddingMedium),
                    actionItems = actionItems
                )
            }
        }
    }
}

@Composable
fun getActionButtonBackgroundColor(dialogActionType: DialogActionType): Color =
    when (dialogActionType) {
        DialogActionType.Neutral -> RumbleCustomTheme.colors.backgroundHighlight
        DialogActionType.Destructive -> fierceRed
        DialogActionType.Positive -> rumbleGreen
    }

@Composable
fun getActionButtonTextColor(dialogActionType: DialogActionType): Color = when (dialogActionType) {
    DialogActionType.Neutral -> RumbleCustomTheme.colors.primary
    DialogActionType.Destructive -> enforcedWhite
    DialogActionType.Positive -> enforcedDarkmo
}

@Composable
private fun DialogTitle(modifier: Modifier = Modifier, title: String?) {
    title?.let {
        Text(
            modifier = modifier,
            text = title,
            style = RumbleTypography.h3,
            color = RumbleCustomTheme.colors.primary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DialogButtonsView(modifier: Modifier = Modifier, actionItems: List<DialogActionItem>) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        actionItems.forEach { item ->
            Row {
                val backgroundColor =
                    getActionButtonBackgroundColor(dialogActionType = item.dialogActionType)
                val textColor =
                    getActionButtonTextColor(dialogActionType = item.dialogActionType)

                ActionButton(
                    modifier = Modifier.widthIn(min = item.width),
                    text = item.text,
                    backgroundColor = backgroundColor,
                    borderColor = backgroundColor,
                    textColor = textColor,
                    onClick = item.action
                )
                if (item.withSpacer)
                    Spacer(modifier = Modifier.width(paddingXSmall))
            }
        }
    }
}

@Composable
private fun DialogTextView(
    modifier: Modifier = Modifier,
    text: String? = null,
    annotatedTextWithActions: AnnotatedStringWithActionsList?,
    onAnnotatedTextClicked: ((annotatedTextWithActions: AnnotatedStringWithActionsList, offset: Int) -> Unit)?,
) {
    text?.let {
        Text(
            modifier = modifier,
            text = text,
            style = RumbleTypography.body1,
            color = RumbleCustomTheme.colors.secondary,
            textAlign = TextAlign.Center
        )
    }
    annotatedTextWithActions?.let {
        ClickableText(
            modifier = modifier,
            text = annotatedTextWithActions.annotatedString,
            style = RumbleTypography.body1.copy(
                color = RumbleCustomTheme.colors.secondary
            ),
            onClick = { offset ->
                onAnnotatedTextClicked?.let {
                    it(
                        annotatedTextWithActions,
                        offset
                    )
                }
            }
        )
    }
}