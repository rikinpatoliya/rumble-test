package com.rumble.ui3.settings.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import com.rumble.R
import com.rumble.commonViews.dialogs.DialogActionItem
import com.rumble.commonViews.dialogs.DialogActionType
import com.rumble.commonViews.dialogs.RumbleAlertDialog
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.theme.RumbleTvTypography.labelBoldTv
import com.rumble.theme.RumbleTvTypography.labelRegularTv
import com.rumble.theme.debugSettingsToggleRadius
import com.rumble.theme.debugSettingsToggleWidth
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXGiant
import com.rumble.ui3.search.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {
    private val viewModel: SettingsHandler by viewModels<SettingsViewModel>()

    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<SettingsFragment> =
        object : BrowseSupportFragment.MainFragmentAdapter<SettingsFragment>(this) {}

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<SettingsFragment> =
        fragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DebugSettings(viewModel)
            }
        }
    }
}

@Composable
fun DebugSettings(settingsHandler: SettingsHandler) {

    val state = settingsHandler.uiState.collectAsStateWithLifecycle().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(paddingXGiant),
        verticalArrangement = Arrangement.spacedBy(
            space = paddingMedium,
            alignment = Alignment.CenterVertically
        )
    ) {
        DebugSettingsToggle(
            text = stringResource(R.string.disable_ads),
            checked = state.disableAds,
            onCheckedChange = settingsHandler::onDisableAdsChanged
        )

        DebugSettingsToggle(
            text = stringResource(R.string.force_ads),
            checked = state.forceAds,
            onCheckedChange = settingsHandler::onForceAdsChanged,
            enabled = state.disableAds.not()
        )

        DebugSettingsToggle(
            text = stringResource(R.string.always_play_debug_ad),
            checked = state.displayDebugAd,
            onCheckedChange = settingsHandler::onDisplayDebugAdChanged,
            enabled = state.disableAds.not()
        )
        val focusDirection = if (state.rumbleSubdomain.canResetSubdomain) {
            FocusDirection.Down
        } else {
            FocusDirection.Up
        }

        ChangeSubdomainInputField(
            value = state.subdomain,
            onValueChange = settingsHandler::onSubdomainChanged,
            onSave = settingsHandler::onUpdateSubdomain,
            focusDirection = focusDirection
        )

        if (state.rumbleSubdomain.canResetSubdomain) {
            ResetCustomSubdomain(onClick = settingsHandler::onResetSubdomain)
        }

        if (state.alertDialogState.show) {
            when (state.alertDialogState.alertDialogReason) {
                is SettingsAlertDialogReason.ChangeSubdomainConfirmationDialog -> {
                    ChangeSubdomainConfirmationDialog(
                        onDismissDialog = settingsHandler::onDismissDialog
                    )
                }
            }
        }
    }
}

@Composable
private fun DebugSettingsToggle(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .width(debugSettingsToggleWidth)
            .clip(shape = RoundedCornerShape(debugSettingsToggleRadius))
            .background(color = enforcedWhite.copy(0.1f))
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox,
                enabled = enabled
            )
            .padding(paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = labelBoldTv,
            color = enforcedWhite,
            modifier = Modifier
                .weight(1f)
                .alpha(if (enabled) 1f else 0.5f)
        )

        Icon(
            painter = painterResource(
                if (checked) {
                    R.drawable.checked_checkbox
                } else {
                    R.drawable.unchecked_checkbox
                }
            ),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(imageSmall)
                .alpha(if (enabled) 1f else 0.5f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeSubdomainInputField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSave: () -> Unit,
    focusDirection: FocusDirection
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    BackHandler {
        hideKeyboard(context)
        focusManager.clearFocus()
    }

    TextField(
        value = value,
        onValueChange = { onValueChange(it) },
        singleLine = true,
        shape = RoundedCornerShape(debugSettingsToggleRadius),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = enforcedWhite.copy(0.1f),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedTextColor = enforcedWhite,
            focusedTextColor = enforcedWhite
        ),
        label = {
            androidx.tv.material3.Text(
                text = stringResource(id = R.string.custom_subdomain),
                style = labelRegularTv.copy(enforcedWhite.copy(alpha = 0.5f))
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                hideKeyboard(context)
                focusManager.clearFocus()
                onSave()
            },
            onPrevious = {
                // required for FireTV to hide keyboard on back pressed
                hideKeyboard(context)
                focusManager.clearFocus()
                focusManager.moveFocus(focusDirection)

            }
        ),
        modifier = modifier
            .width(debugSettingsToggleWidth)
            .focusRequester(focusRequester)
    )
}

@Composable
private fun ResetCustomSubdomain(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(debugSettingsToggleWidth)
            .clip(shape = RoundedCornerShape(debugSettingsToggleRadius))
            .background(color = enforcedWhite.copy(0.1f))
            .clickable { onClick() }
            .padding(paddingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.reset_custom_subdomain),
            style = labelBoldTv,
            color = enforcedWhite,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ChangeSubdomainConfirmationDialog(onDismissDialog: () -> Unit) {
    RumbleAlertDialog(
        onDismissRequest = onDismissDialog,
        text = stringResource(id = R.string.subdomain_changed_restart_app),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(id = R.string.ok),
                withSpacer = true,
                action = onDismissDialog,
                dialogActionType = DialogActionType.Positive
            )
        )
    )
}