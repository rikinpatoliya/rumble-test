package com.rumble.domain.camera.domain.usecases

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetMediaFileUriUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(mediaFilePath: String): String {
        val inputVideoUri = Uri.parse(mediaFilePath)
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = context.contentResolver.query(inputVideoUri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
        cursor?.moveToFirst()
        val path = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return path ?: ""
    }
}