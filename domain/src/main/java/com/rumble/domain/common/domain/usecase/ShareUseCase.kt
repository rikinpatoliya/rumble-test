package com.rumble.domain.common.domain.usecase

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ShareUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {
    private val tag = "ShareUseCase"

    operator fun invoke(text: String, title: String = ""): Boolean {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_TITLE, title)
        }
        return try {
            val shareIntent = Intent.createChooser(intent, null).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(shareIntent)
            true
        } catch (e: ActivityNotFoundException) {
            unhandledErrorUseCase(tag, e)
            false
        }
    }
}