package com.rumble.ui3.settings.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rumble.databinding.V3SettingsCopyrightBinding
import com.rumble.leanback.BrowseSupportFragment


class CopyrightFragment : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    /***/
    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<CopyrightFragment> =
        object : BrowseSupportFragment.MainFragmentAdapter<CopyrightFragment>(this) {}

    /***/
    private var _binding: V3SettingsCopyrightBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<CopyrightFragment> = fragmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V3SettingsCopyrightBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}