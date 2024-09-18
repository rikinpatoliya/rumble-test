package com.rumble.ui3.subscriptions.v4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.rumble.R
import com.rumble.databinding.V4SubscriptionsNoSubscriptionsBinding
import com.rumble.leanback.BrowseSupportFragment
import com.rumble.ui3.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SubscriptionsNoSubscriptionsFragmentV4 : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    /***/
    private val fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<SubscriptionsNoSubscriptionsFragmentV4> =
        object : BrowseSupportFragment.MainFragmentAdapter<SubscriptionsNoSubscriptionsFragmentV4>(this) {}
    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<SubscriptionsNoSubscriptionsFragmentV4> = fragmentAdapter

    /***/
    private var _binding: V4SubscriptionsNoSubscriptionsBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!
    /***/
    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V4SubscriptionsNoSubscriptionsBinding.inflate(inflater)
        binding.mainViewModel = viewModel
        binding.loginButton.setOnClickListener {
            Navigation.findNavController(requireView()).navigate(R.id.recommendedChannelsScreenActivity)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}