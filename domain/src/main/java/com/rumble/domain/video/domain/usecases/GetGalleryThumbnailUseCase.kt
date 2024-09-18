package com.rumble.domain.video.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.GalleryVideoEntity
import com.rumble.network.di.IoDispatcher
import com.rumble.utils.RumbleConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val TAG = "GetGalleryThumbnailUseCase"

class GetGalleryThumbnailUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {
    suspend operator fun invoke(galleryVideoEntity: GalleryVideoEntity): Bitmap? {
        return withContext(ioDispatcher) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    context.contentResolver.loadThumbnail(
                        galleryVideoEntity.videoUri,
                        Size(RumbleConstants.GALLERY_THUMB_SIZE, RumbleConstants.GALLERY_THUMB_SIZE),
                        null
                    )
                } else {
                    MediaStore.Video.Thumbnails.getThumbnail(
                        context.contentResolver,
                        galleryVideoEntity.galleryVideoId, MediaStore.Video.Thumbnails.MINI_KIND, null
                    )
                }
            } catch (e: Exception) {
                unhandledErrorUseCase(TAG, e)
                null
            }
        }
    }
}