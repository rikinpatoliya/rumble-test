package com.rumble.domain.common.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OpenUriUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {
    operator fun invoke(
        tag: String,
        uri: String,
    ) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)).also {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            unhandledErrorUseCase(tag, e)
        }
    }
}