package com.rumble.domain.common.domain.usecase

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SendEmailUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {
    private val tag = "SendEmailUseCase"

    operator fun invoke(email: String, subject: String? = null, body: String? = null): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            body?.let { putExtra(Intent.EXTRA_TEXT, it) }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        return try {
            context.startActivity(intent)
            true
        } catch (e : ActivityNotFoundException) {
            unhandledErrorUseCase(tag, e)
            false
        }
    }
}