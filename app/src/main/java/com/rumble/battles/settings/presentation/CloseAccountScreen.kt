package com.rumble.battles.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.CloseAccountActionButtonTag
import com.rumble.battles.CloseAccountTag
import com.rumble.battles.CloseAccountTextTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.theme.RumbleTypography
import com.rumble.theme.bottomBarHeight
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXLarge
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusXLarge

@Composable
fun CloseAccountScreen(
    closeAccountHandler: CloseAccountHandler,
    onBackClick: () -> Unit,
) {
    val state by closeAccountHandler.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = context) {
        closeAccountHandler.vmEvents.collect { event ->
            when (event) {
                is CloseAccountVmEvent.Error -> {
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
            .testTag(CloseAccountTag)
            .fillMaxSize()
            .padding(bottom = bottomBarHeight)
            .background(MaterialTheme.colors.background)
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.close_your_rumble_account),
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            onBackClick = onBackClick
        )
        CloseAccountContent(
            closeAccountHandler = closeAccountHandler
        )
    }
    if (state.loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = false) {}
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    if (state.alertDialogState.show) {
        state.alertDialogResponseData?.let {
            CloseAccountResultDialog(success = it.success) { closeAccountHandler.onDismissDialog() }
        } ?: run {
            CloseAccountConfirmationDialog(
                closeAccount = { closeAccountHandler.onCloseAccount() },
                onDismissDialog = { closeAccountHandler.onDismissDialog() }
            )
        }
    }
    RumbleSnackbarHost(snackBarHostState)
}

@Composable
fun CloseAccountContent(
    closeAccountHandler: CloseAccountHandler
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingMedium)
            .clip(RoundedCornerShape(radiusMedium))
            .background(MaterialTheme.colors.onSecondary)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.close_account_description),
                modifier = Modifier
                    .padding(
                        start = paddingMedium,
                        end = paddingMedium
                    )
                    .testTag(CloseAccountTextTag),
                style = RumbleTypography.h4,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier
                    .padding(top = paddingXLarge)
                    .testTag(CloseAccountActionButtonTag),
                shape = RoundedCornerShape(radiusXLarge),
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = fierceRed
                ),
                onClick = { closeAccountHandler.onProceed() }
            ) {
                Text(
                    modifier = Modifier.padding(
                        top = paddingMedium,
                        bottom = paddingMedium,
                        start = paddingXLarge,
                        end = paddingXLarge
                    ),
                    text = stringResource(id = R.string.proceed_to_next_step),
                    style = RumbleTypography.h3,
                    color = enforcedWhite
                )
            }
        }
    }
}

@Composable
fun CloseAccountConfirmationDialog(
    closeAccount: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    RumbleAlertDialog(
        onDismissRequest = onDismissDialog,
        title = stringResource(id = R.string.close_account),
        text = stringResource(id = R.string.close_account_confirmation_dialog_text),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(id = R.string.cancel),
                withSpacer = true,
                action = onDismissDialog
            ),
            DialogActionItem(
                text = stringResource(id = R.string.yes),
                dialogActionType = DialogActionType.Destructive,
                action = closeAccount
            ),
        )
    )
}

@Composable
fun CloseAccountResultDialog(
    success: Boolean,
    onDismissDialog: () -> Unit,
) {
    RumbleAlertDialog(
        onDismissRequest = onDismissDialog,
        title = stringResource(id = R.string.close_account),
        text = stringResource(id = if (success) R.string.please_check_your_email_for_confirmation else R.string.generic_error_message_try_later),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(id = R.string.okay),
                dialogActionType = DialogActionType.Positive,
                action = onDismissDialog
            ),
        )
    )
}