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
import com.rumble.battles.SettingsSubdomainTag
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.MainActionBottomCardView
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleInputFieldView
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium

@Composable
fun ChangeSubdomainScreen(
    changeSubdomainHandler: ChangeSubdomainHandler,
    onBackClick: () -> Unit,
) {
    val state by changeSubdomainHandler.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = context) {
        changeSubdomainHandler.vmEvents.collect { event ->
            when (event) {
                is ChangeSubdomainScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.testTag(SettingsSubdomainTag)
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.change_subdomain),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .systemBarsPadding(),
            onBackClick = onBackClick,
        )
        ChangeSubdomainView(state.subdomain, changeSubdomainHandler)
        Spacer(modifier = Modifier.weight(1F))
        MainActionBottomCardView(
            modifier = Modifier,
            title = stringResource(id = R.string.update),
            onClick = { changeSubdomainHandler.onUpdate() }
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
        ChangeSubdomainConfirmationDialog(
            onDismissDialog = { changeSubdomainHandler.onDismissDialog() }
        )
    }
    RumbleSnackbarHost(snackBarHostState)
}

@Composable
fun ChangeSubdomainConfirmationDialog(
    onDismissDialog: () -> Unit,
) {
    RumbleAlertDialog(
        onDismissRequest = onDismissDialog,
        title = "",
        text = stringResource(id = R.string.subdomain_changed_restart_app),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(id = R.string.ok),
                withSpacer = true,
                action = onDismissDialog,
                dialogActionType = DialogActionType.Positive,
            ),
        )
    )
}

@Composable
private fun ChangeSubdomainView(
    subdomain: String,
    changeSubdomainHandler: ChangeSubdomainHandler
) {
    RumbleInputFieldView(
        modifier = Modifier
            .padding(
                top = paddingLarge,
                start = paddingMedium,
                end = paddingMedium
            ),
        label = stringResource(id = R.string.subdomain).uppercase(),
        initialValue = subdomain,
        onValueChange = changeSubdomainHandler::onSubdomainChanged,
        hasError = false,
        errorMessage = ""
    )
}