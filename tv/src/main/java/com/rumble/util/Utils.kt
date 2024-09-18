package com.rumble.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.rumble.R
import com.rumble.databinding.DialogMatureContentBinding
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.player.VideoPlaybackActivity
import com.rumble.ui3.channel.details.v4.ChannelDetailsActivityV4
import com.rumble.util.Constant.FINISH_ACTION
import java.util.Date

object Utils {
    fun shouldRefreshContent(lastApiCallTime: Date?): Boolean {
        return lastApiCallTime?.let { apiCallTime ->
            val currentTime = Date()
            val timeDifference = currentTime.time - apiCallTime.time
            timeDifference > Constant.REFRESH_CONTENT_DURATION
        } ?: true
    }

    fun showMatureContentDialog(context: Context, positiveButtonClick: () -> Unit) {
        val matureContentDialog = Dialog(context)
        val bindings: DialogMatureContentBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_mature_content,
            null,
            false
        )
        matureContentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        matureContentDialog.setContentView(bindings.root)
        matureContentDialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        matureContentDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        matureContentDialog.setCancelable(true)

        bindings.positiveButton.setOnClickListener {
            positiveButtonClick()
            matureContentDialog.dismiss()
        }

        bindings.negativeButton.setOnClickListener {
            matureContentDialog.dismiss()
        }
        matureContentDialog.show()
    }

    fun navigateToVideoPlayback(context: Context, item: VideoEntity, fromChannel: String = "") {
        context.sendBroadcast(Intent(FINISH_ACTION))
        val intent = Intent(context, VideoPlaybackActivity::class.java)
        intent.putExtra(Constant.PLAYBACK_ACTIVITY_ARGS, item)
        intent.putExtra(Constant.PLAYBACK_ACTIVITY_FROM_CHANNEL, fromChannel)
        intent.flags = Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
        context.startActivity(intent)
    }

    fun navigateToChannelDetails(context: Context, item: ChannelDetailsEntity) {
        val intent = Intent(context, ChannelDetailsActivityV4::class.java)
        intent.putExtra(Constant.CHANNEL_ITEM_ARGS, item)
        intent.putExtra(Constant.SHOW_LOGO_ARGS, true)
        context.startActivity(intent)
    }
}