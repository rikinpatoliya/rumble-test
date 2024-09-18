package com.rumble.ui3.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.leanback.app.ProgressBarManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.rumble.R
import com.rumble.databinding.V3UserNotLoggedInBinding
import com.rumble.ui3.user.UserFragmentStates
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserNotLoggedInFragment : Fragment() {

    /***/
    private val progressBarManager by lazy {
        ProgressBarManager().apply {
            this.initialDelay = 0
        }
    }
    /***/
    private var _binding: V3UserNotLoggedInBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!
    /***/
    private val viewModel: UserNotLoggedInViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V3UserNotLoggedInBinding.inflate(inflater)
        progressBarManager.setRootView(binding.root as ViewGroup)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        viewModel.liveData.observe(viewLifecycleOwner) {
            binding.requestCode = it
        }
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UserFragmentStates.Error ->  {
                        progressBarManager.hide()
                        Navigation.findNavController(view).navigate(R.id.userAuthenticationErrorFragment)
                    }
                    is UserFragmentStates.Loading -> {
                        progressBarManager.show()
                    }
                    is UserFragmentStates.LoggedIn -> {
                        progressBarManager.hide()
                        Navigation.findNavController(view).navigate(R.id.userLoggedInFragment)
                    }
                    is UserFragmentStates.NotLoggedIn -> {
                        progressBarManager.hide()
                    }
                }
            }
        }
        viewModel.requestCodeIfNeeded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarManager.setRootView(null)
        _binding = null
    }
}