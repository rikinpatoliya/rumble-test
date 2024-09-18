package com.rumble.ui3.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.rumble.R
import com.rumble.databinding.V3UserLoggedInBinding
import com.rumble.ui3.channel.ChannelStates
import com.rumble.util.isNetworkConnected
import com.rumble.util.showAlert
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class UserLoggedInFragment : Fragment() {

    /***/
    private var _binding: V3UserLoggedInBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!
    /***/
    private val viewModel: UserLoggedInViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V3UserLoggedInBinding.inflate(inflater)
        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logoutButton.setOnClickListener {
            if (requireContext().isNetworkConnected){
                viewModel.onLogoutClick()
                ChannelStates.reloadChannelData = true
                Navigation.findNavController(it).navigate(R.id.userNotLoggedInFragment)
            }else{
                parentFragmentManager.showAlert(getString(R.string.no_internet), true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}