package com.rumble.ui3.user.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.rumble.R
import com.rumble.databinding.V3UserAuthenticationErrorBinding
import com.rumble.util.isNetworkConnected
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class UserAuthenticationErrorFragment : Fragment() {

    /***/
    private var _binding: V3UserAuthenticationErrorBinding? = null
    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = V3UserAuthenticationErrorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.refreshButton.setOnClickListener {
            if (requireContext().isNetworkConnected){
                Navigation.findNavController(requireView()).navigate(R.id.userNotLoggedInFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}