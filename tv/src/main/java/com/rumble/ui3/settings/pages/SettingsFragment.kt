package com.rumble.ui3.settings.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rumble.databinding.SettingsFragmentBinding
import com.rumble.leanback.BrowseSupportFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {
    private val viewModel: SettingsHandler by viewModels<SettingsViewModel>()

    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<SettingsFragment> =
        object : BrowseSupportFragment.MainFragmentAdapter<SettingsFragment>(this) {}

    private var _binding: SettingsFragmentBinding? = null

    private val binding get() = _binding!!

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<SettingsFragment> =
        fragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SettingsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.disableAds.isChecked = state.disableAds
                binding.forceAds.isEnabled = state.disableAds.not()
                binding.displayDebugAd.isEnabled = state.disableAds.not()
                binding.forceAds.isChecked = state.forceAds
                binding.displayDebugAd.isChecked = state.displayDebugAd
            }
        }

        binding.onDisableAdsCheckChanged =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                viewModel.onDisableAdsChanged(isChecked)
            }

        binding.onForceAdsCheckChanged =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                viewModel.onForceAdsChanged(isChecked)
            }

        binding.onDisplayDebugAdCheckChanged =
            CompoundButton.OnCheckedChangeListener { _, isChecked ->
                viewModel.onDisplayDebugAdChanged(isChecked)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}