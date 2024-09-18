package com.rumble.ui3.settings.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rumble.databinding.V3SettingsTermsConditionsBinding
import com.rumble.leanback.BrowseSupportFragment


class TermsAndConditionsFragment : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    /***/
    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<TermsAndConditionsFragment> =
        object : BrowseSupportFragment.MainFragmentAdapter<TermsAndConditionsFragment>(this) {}

    /***/
    private var _binding: V3SettingsTermsConditionsBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<TermsAndConditionsFragment> = fragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V3SettingsTermsConditionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}