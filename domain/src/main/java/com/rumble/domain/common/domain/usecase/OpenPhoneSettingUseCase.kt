package com.rumble.domain.common.domain.usecase

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "OpenPhoneSettingUseCase"

class OpenPhoneSettingUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {
    operator fun invoke() {
        try {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.fromParts("package", context.packageName, null)
            })
        } catch (e: ActivityNotFoundException) {
            unhandledErrorUseCase(TAG, e)
        }
    }
}