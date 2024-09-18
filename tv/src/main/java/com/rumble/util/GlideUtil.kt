package com.rumble.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.rumble.di.qualifier.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class GlideUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun loadUrlIntoBitmap(url: String) = withContext(ioDispatcher) {

        suspendCoroutine<Bitmap> { continuation ->
            Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap,  transition: Transition<in Bitmap?>?) {
                        continuation.resume(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        continuation.resumeWithException(Exception("onLoadCleared"))
                    }
                })
        }


    }
}