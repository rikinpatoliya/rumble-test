package com.rumble.ui3.settings.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rumble.databinding.V3SettingsPrivacyPolicyBinding
import com.rumble.leanback.BrowseSupportFragment


class PrivacyPolicyFragment : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    /***/
    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<PrivacyPolicyFragment> =
        object : BrowseSupportFragment.MainFragmentAdapter<PrivacyPolicyFragment>(this) {}

    /***/
    private var _binding: V3SettingsPrivacyPolicyBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<PrivacyPolicyFragment> = fragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V3SettingsPrivacyPolicyBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}