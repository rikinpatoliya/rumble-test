package com.rumble.ui3.channels

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.ui3.channel.details.v4.ChannelDetailsActivityV4
import com.rumble.util.Constant
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendedChannelsScreenActivity : ComponentActivity() {

    private val viewModel: RecommendedChannelsHandler by viewModels<RecommendedChannelsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecommendedChannelsScreen(
                viewModel = viewModel,
                onNavigateToChannelDetails = {
                    navigateToChannelDetails(it)
                },
                onBack = {
                    onBackPressedDispatcher.onBackPressed()
                })
        }
    }

    private fun navigateToChannelDetails(channel: CreatorEntity) {
        val intent = Intent(this, ChannelDetailsActivityV4::class.java).apply {
            putExtra(Constant.CHANNEL_ITEM_ARGS, channel)
            putExtra(Constant.SHOW_LOGO_ARGS, true)
        }
        startActivity(intent)
    }
}