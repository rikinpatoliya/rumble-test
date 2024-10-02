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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.SettingsPasswordTag
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.MainActionBottomCardView
import com.rumble.battles.commonViews.PasswordView
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium

@Composable
fun ChangePasswordScreen(
    changePasswordHandler: ChangePasswordHandler,
    onBackClick: () -> Unit,
) {
    val state by changePasswordHandler.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = context) {
        changePasswordHandler.vmEvents.collect { event ->
            when (event) {
                is ChangePasswordScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .testTag(SettingsPasswordTag)
            .fillMaxSize()
            .systemBarsPadding()
            .background(MaterialTheme.colors.background)
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.change_password),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .systemBarsPadding(),
            onBackClick = onBackClick
        )
        ChangePasswordView(state, changePasswordHandler)
        Spacer(modifier = Modifier.weight(1F))
        MainActionBottomCardView(
            title = stringResource(id = R.string.update),
            onClick = { changePasswordHandler.onUpdate() }
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
                onDismissRequest = { changePasswordHandler.onDismissDialog() },
                title = if (it.success) stringResource(id = R.string.password_updated) else stringResource(id = R.string.unable_to_update),
                text = it.message ?: stringResource(id = R.string.generic_error_message_try_later),
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(id = R.string.ok),
                        action = { changePasswordHandler.onDismissDialog() },
                        dialogActionType = DialogActionType.Positive,
                    )
                )
            )
        }
    }
    RumbleSnackbarHost(snackBarHostState)
}

@Composable
private fun ChangePasswordView(
    state: ChangePasswordUIState,
    changePasswordHandler: ChangePasswordHandler
) {
    PasswordView(
        modifier = Modifier.padding(
            top = paddingLarge,
            start = paddingMedium,
            end = paddingMedium
        ),
        label = stringResource(id = R.string.new_password).uppercase(),
        labelColor = MaterialTheme.colors.primary,
        onValueChange = changePasswordHandler::onNewPasswordChanged,
        hasError = state.newPasswordError,
        errorMessage = stringResource(id = R.string.password_at_least_8_characters)
    )
    PasswordView(
        modifier = Modifier.padding(paddingMedium),
        label = stringResource(id = R.string.current_password).uppercase(),
        labelColor = MaterialTheme.colors.primary,
        onValueChange = changePasswordHandler::onCurrentPasswordChanged,
        hasError = state.currentPasswordError,
        errorMessage = stringResource(id = R.string.enter_current_password)
    )
}