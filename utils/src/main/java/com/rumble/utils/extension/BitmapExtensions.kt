package com.rumble.utils.extension

import android.graphics.Bitmap

fun Bitmap.scaleToMaxWidth(maxWidth: Int): Bitmap {
    val width = width

    return if (width > maxWidth) {
        val height = height
        val aspectRatio: Double = height / width.toDouble()
        val outHeight = (maxWidth * aspectRatio).toInt()

        Bitmap.createScaledBitmap(this, maxWidth, outHeight, false)
    } else {
        this
    }
}