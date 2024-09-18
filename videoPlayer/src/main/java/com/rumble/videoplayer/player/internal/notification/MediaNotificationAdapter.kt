package com.rumble.videoplayer.player.internal.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

@UnstableApi
internal class MediaNotificationAdapter(
    private val context: Context
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.displayTitle ?: ""

    override fun createCurrentContentIntent(player: Player): PendingIntent? = null

    override fun getCurrentContentText(player: Player): CharSequence =
        player.mediaMetadata.description ?: ""

    override fun getCurrentSubText(player: Player): CharSequence =
        player.mediaMetadata.subtitle ?: ""


    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        Glide.with(context)
            .asBitmap()
            .load(player.mediaMetadata.artworkUri)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) = Unit
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    callback.onBitmap(resource)
                }
            })
        return null
    }
}