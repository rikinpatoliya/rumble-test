package com.rumble.ui3.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.rumble.R
import com.rumble.leanback.BrowseSupportFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserFragment : Fragment(), BrowseSupportFragment.MainFragmentAdapterProvider {

    /***/
    private lateinit var navController: NavController
    /***/
    private val viewModel: UserViewModel by activityViewModels()
    /***/
    private val mainFragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<UserFragment> =
        object : BrowseSupportFragment.MainFragmentAdapter<UserFragment>(this) {}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.v3_user_main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val nestedNavHostFragment = childFragmentManager.findFragmentById(R.id.user_nav_host_fragment) as? NavHostFragment
        navController = nestedNavHostFragment!!.navController

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UserFragmentStates.Error ->  {}
                    is UserFragmentStates.Loading -> {}
                    is UserFragmentStates.LoggedIn -> {
                        navController.navigate(R.id.userLoggedInFragment)
                    }
                    is UserFragmentStates.NotLoggedIn -> {
                        navController.navigate(R.id.userNotLoggedInFragment)
                    }
                }
            }
        }
        viewModel.getUiState()
    }

    override fun getMainFragmentAdapter(): BrowseSupportFragment.MainFragmentAdapter<UserFragment> = mainFragmentAdapter

}