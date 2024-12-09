package com.rumble.battles.commonViews.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.rumble.battles.R
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.AnnotatedTextAction
import com.rumble.theme.commentActionButtonWidth
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants.TAG_URL

@Composable
fun ConsentDialog(
    handler: RumbleActivityHandler,
) {
    RumbleAlertDialog(
        onDismissRequest = handler::onDismissDialog,
        title = stringResource(id = R.string.accept_rumble_terms),
        annotatedTextWithActions = buildConsentStringWithActions(handler),
        onAnnotatedTextClicked = handler::onAnnotatedTextClicked,
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(R.string.decline),
                dialogActionType = DialogActionType.Neutral,
                withSpacer = true,
                width = commentActionButtonWidth,
                action = handler::onDismissDialog
            ),
            DialogActionItem(
                text = stringResource(R.string.accept),
                dialogActionType = DialogActionType.Positive,
                width = commentActionButtonWidth,
                action = handler::onAcceptTos
            )
        )
    )
}

@Composable
private fun buildConsentStringWithActions(
    handler: RumbleActivityHandler,
): AnnotatedStringWithActionsList {
    val actionList = mutableListOf<AnnotatedTextAction>()
    val text = buildAnnotatedString {
        append(stringResource(id = R.string.consent_dialog_message))
        append(" ")
        withStyle(style = SpanStyle(color = rumbleGreen)) {
            actionList.add(AnnotatedTextAction(TAG_URL) { uri ->
                handler.onOpenWebView(uri)
            })
            val startIndexTerms = this.length
            append(stringResource(id = R.string.terms_of_service_capital))
            val endIndexTerms = this.length
            addStringAnnotation(
                tag = TAG_URL,
                annotation = stringResource(id = R.string.rumble_terms_and_conditions_url),
                start = startIndexTerms,
                end = endIndexTerms
            )
        }
        append(" ")
        append(stringResource(id = R.string.and))
        append(" ")
        withStyle(style = SpanStyle(color = rumbleGreen)) {
            actionList.add(AnnotatedTextAction(TAG_URL) { uri ->
                handler.onOpenWebView(uri)
            })
            val startIndexPrivacy = this.length
            append(stringResource(id = R.string.privacy_policy))
            val endIndexPrivacy = this.length
            addStringAnnotation(
                tag = TAG_URL,
                annotation = stringResource(id = R.string.rumbles_privacy_policy_url),
                start = startIndexPrivacy,
                end = endIndexPrivacy
            )
        }
    }
    return AnnotatedStringWithActionsList(text, actionList)
}