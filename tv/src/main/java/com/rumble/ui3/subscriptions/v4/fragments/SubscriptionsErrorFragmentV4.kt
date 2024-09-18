package com.rumble.ui3.subscriptions.v4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rumble.databinding.V3SubscriptionsListBinding
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.ui3.subscriptions.v4.SubscriptionsViewModelV4
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SubscriptionsErrorFragmentV4 : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {
    /***/
    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<SubscriptionsErrorFragmentV4> =
        object : BrowseSupportFragment.MainFragmentAdapter<SubscriptionsErrorFragmentV4>(this) {}

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<SubscriptionsErrorFragmentV4> =
        fragmentAdapter

    private val subscriptionsViewModel: SubscriptionsViewModelV4 by activityViewModels()

    private var _binding: V3SubscriptionsListBinding? = null

    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V3SubscriptionsListBinding.inflate(inflater)

        binding.refreshButton.setOnClickListener {
            subscriptionsViewModel.getUiState()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}