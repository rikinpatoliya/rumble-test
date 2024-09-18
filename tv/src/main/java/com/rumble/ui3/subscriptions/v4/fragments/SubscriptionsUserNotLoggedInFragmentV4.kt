package com.rumble.ui3.subscriptions.v4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.rumble.databinding.V4SubscriptionsUserNotLoggedInBinding
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.ui3.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SubscriptionsUserNotLoggedInFragmentV4 : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    /***/
    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<SubscriptionsUserNotLoggedInFragmentV4> =
        object : BrowseSupportFragment.MainFragmentAdapter<SubscriptionsUserNotLoggedInFragmentV4>(this) {}
    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<SubscriptionsUserNotLoggedInFragmentV4> = fragmentAdapter
    /***/
    private var _binding: V4SubscriptionsUserNotLoggedInBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!
    /***/
    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V4SubscriptionsUserNotLoggedInBinding.inflate(inflater)
        binding.mainViewModel = viewModel
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}