package com.rumble.domain.video.domain.usecases

import android.graphics.Bitmap
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import javax.inject.Inject

private const val TAG = "GeneratePlaceholderThumbnailsUseCase"

class GeneratePlaceholderThumbnailsUseCase @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {
    operator fun invoke(quantity: Int, width: Int = 150, height: Int = 150): List<Bitmap> {
        return try {
            val listOfThumbnail = mutableListOf<Bitmap>()
            (1..quantity).map {
                val bitmap = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.ARGB_8888
                )
                listOfThumbnail.add(bitmap)
            }
            listOfThumbnail
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
            emptyList()
        }
    }
}