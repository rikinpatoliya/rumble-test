package com.rumble.domain.common.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.network.di.AppStoreUrl
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "OpenPlayStoreUseCase"

class OpenPlayStoreUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    @AppStoreUrl private val appStoreUrl: String,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
) {
    operator fun invoke() {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(appStoreUrl)
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
        }
    }
}