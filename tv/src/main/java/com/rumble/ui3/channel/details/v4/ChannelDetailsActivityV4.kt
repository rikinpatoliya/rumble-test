package com.rumble.ui3.channel.details.v4

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.rumble.R
import com.rumble.databinding.ActivityChannelDetailsBinding
import com.rumble.network.connection.InternetConnectionState
import com.rumble.ui3.main.InternetConnectionLostDialogFragment
import com.rumble.ui3.search.SearchItemsPosition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ChannelDetailsActivityV4 : FragmentActivity() {

    private var _binding: ActivityChannelDetailsBinding? = null

    private val viewModel: ChannelDetailsViewModelV4 by viewModels()

    /** This property is only valid between onCreateView and onDestroyView. */
    private val binding get() = _binding!!

    private lateinit var dialogInternet: InternetConnectionLostDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_channel_details)
        SearchItemsPosition.refreshChannelDetailInRowItem = false
        intent.extras?.let {
            val channel = ChannelDetailsActivityV4Args.fromBundle(it).channel
            val showLogo = ChannelDetailsActivityV4Args.fromBundle(it).showLogo

            val channelDetailsFragment =
                ChannelDetailsFragmentV4.getInstance(channel, showLogo, true, isCachingSupported = false)
            supportFragmentManager.beginTransaction()
                .replace(R.id.channelDetailsContainer, channelDetailsFragment)
                .disallowAddToBackStack()
                .commit()
        }

        dialogInternet = supportFragmentManager
            .findFragmentByTag(InternetConnectionLostDialogFragment::class.java.simpleName)
                as? InternetConnectionLostDialogFragment ?: InternetConnectionLostDialogFragment()

        viewModel.connectionState.observe(this) {
            when {
                it == InternetConnectionState.LOST && dialogInternet.isVisible.not() -> {
                    dialogInternet.show(
                        supportFragmentManager,
                        InternetConnectionLostDialogFragment::class.java.simpleName
                    )
                }
                it == InternetConnectionState.CONNECTED && dialogInternet.isAdded -> {
                    dialogInternet.dismiss()
                }
            }
        }

    }
}