package com.rumble.battles.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.SettingsEmailTag
import com.rumble.battles.commonViews.MainActionBottomCardView
import com.rumble.battles.commonViews.PasswordView
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleInputFieldView
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium

@Composable
fun ChangeEmailScreen(
    changeEmailHandler: ChangeEmailHandler,
    contentHandler: ContentHandler,
    onBackClick: () -> Unit,
) {
    val state by changeEmailHandler.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        changeEmailHandler.vmEvents.collect { event ->
            when (event) {
                is ChangeEmailScreenVmEvent.Error -> {
                    contentHandler.onError(event.errorMessage)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .testTag(SettingsEmailTag)
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colors.background)
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.change_email),
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            onBackClick = onBackClick,
        )
        ChangeEmailView(state, changeEmailHandler)
        Spacer(modifier = Modifier.weight(1F))
        MainActionBottomCardView(
            title = stringResource(id = R.string.update),
            onClick = { changeEmailHandler.onUpdate() }
        )
    }
    if (state.loading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    if (state.alertDialogState.show) {
        state.alertDialogResponseData?.let {
            RumbleAlertDialog(
                onDismissRequest = { changeEmailHandler.onDismissDialog() },
                title = if (it.success) stringResource(id = R.string.email_updated) else stringResource(id = R.string.unable_to_update),
                text = it.message ?: stringResource(id = R.string.generic_error_message_try_later),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(id = R.string.ok),
                        action = { changeEmailHandler.onDismissDialog() },
                        dialogActionType = DialogActionType.Positive,
                    )
                )
            )
        }
    }
}

@Composable
private fun ChangeEmailView(state: EmailUIState, changeEmailHandler: ChangeEmailHandler) {
    RumbleInputFieldView(
        modifier = Modifier
            .padding(
                top = paddingLarge,
                start = paddingMedium,
                end = paddingMedium
            ),
        label = stringResource(id = R.string.new_email).uppercase(),
        onValueChange = changeEmailHandler::onEmailChanged,
        hasError = state.emailError,
        errorMessage = when (state.emailErrorType) {
            is EmailError.Invalid -> stringResource(id = R.string.enter_valid_email)
            is EmailError.SameAsCurrent -> stringResource(id = R.string.email_cannot_be_same_as_current)
            else -> ""
        }
    )
    PasswordView(
        modifier = Modifier.padding(paddingMedium),
        label = stringResource(id = R.string.password_label),
        labelColor = MaterialTheme.colors.primary,
        onValueChange = changeEmailHandler::onPasswordChanged,
        hasError = state.passwordError,
        errorMessage = stringResource(id = R.string.enter_current_password)
    )
}