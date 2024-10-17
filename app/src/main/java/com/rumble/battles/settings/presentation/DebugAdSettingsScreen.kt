package com.rumble.battles.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.commonViews.MainActionBottomCardView
import com.rumble.battles.commonViews.RadioButton
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleInputFieldView
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.domain.settings.domain.domainmodel.DebugAdType
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.imageSmall
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXSmall

@Composable
fun DebugAdSettingsScreen(
    debugAdSettingsHandler: DebugAdSettingsHandler,
    onBackClick: () -> Unit,
) {
    val state by debugAdSettingsHandler.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = context) {
        debugAdSettingsHandler.vmEvents.collect { event ->
            when (event) {
                is DebugAdSettingsScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }
            }
        }
    }

    if (state.initialFetch.not()) {
        Column(
            modifier = Modifier.systemBarsPadding()
        ) {
            RumbleBasicTopAppBar(
                title = stringResource(id = R.string.ad_settings_title),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .systemBarsPadding(),
                onBackClick = onBackClick,
            )
            Spacer(modifier = Modifier.height(paddingLarge))
            DebugAdSettingsView(state, debugAdSettingsHandler)
            Spacer(modifier = Modifier.weight(1F))
            MainActionBottomCardView(
                title = stringResource(id = R.string.update),
                onClick = { debugAdSettingsHandler.onUpdate() }
            )
        }
    }
    if (state.loading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    if (state.alertDialogState.show) {
        DebugAdSettingsConfirmationDialog(
            onDismissDialog = { debugAdSettingsHandler.onDismissDialog() }
        )
    }
    RumbleSnackbarHost(snackBarHostState)
}

@Composable
private fun DebugAdSettingsConfirmationDialog(
    onDismissDialog: () -> Unit,
) {
    RumbleAlertDialog(
        onDismissRequest = onDismissDialog,
        title = "",
        text = stringResource(id = R.string.ad_settings_updated),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(id = R.string.ok),
                withSpacer = true,
                action = onDismissDialog,
                dialogActionType = DialogActionType.Positive,
            )
        )
    )
}

@Composable
private fun DebugAdSettingsView(
    state: DebugAdSettingsUIState,
    debugAdSettingsHandler: DebugAdSettingsHandler
) {
    DebugAdType.entries.forEach { type ->
        DebugAdSettingOption(
            text = when (type) {
                DebugAdType.REAL_AD -> stringResource(id = R.string.play_real_ads)
                DebugAdType.DEBUG_AD -> stringResource(id = R.string.play_debug_ad)
                DebugAdType.CUSTOM_AD_TAG -> stringResource(id = R.string.play_custom_ad_tag)
            },
            selected = state.debugAdType == type,
            onClick = { debugAdSettingsHandler.onDebugAdTypeChanged(type) }
        )
    }

    CustomAdTagField(
        customAdTag = state.customAdTag,
        customAdTagError = state.customAdTagError,
        debugAdSettingsHandler = debugAdSettingsHandler
    )
}

@Composable
private fun DebugAdSettingOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = paddingMedium, vertical = paddingXXSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(paddingSmall)
    ) {
        RadioButton(selected = selected, modifier = Modifier.size(imageSmall))

        Text(
            text = text,
            style = h4,
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
private fun CustomAdTagField(
    customAdTag: String,
    customAdTagError: Boolean,
    debugAdSettingsHandler: DebugAdSettingsHandler
) {
    RumbleInputFieldView(
        modifier = Modifier
            .padding(
                top = paddingXXSmall,
                start = imageSmall + paddingSmall + paddingMedium,
                end = paddingMedium
            ),
        label = stringResource(id = R.string.ad_tag).uppercase(),
        initialValue = customAdTag,
        onValueChange = debugAdSettingsHandler::onCustomAdTagChanged,
        hasError = customAdTagError,
        errorMessage = stringResource(R.string.ad_tag_required),
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        )
    )
}