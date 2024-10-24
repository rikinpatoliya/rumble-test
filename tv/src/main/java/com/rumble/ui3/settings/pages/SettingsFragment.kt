package com.rumble.ui3.settings.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.R
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.theme.RumbleTvTypography.labelBoldTv
import com.rumble.theme.enforcedWhite
import com.rumble.theme.imageSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXGiant
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
            .width(360.dp)
            .clip(shape = RoundedCornerShape(8.dp))
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
