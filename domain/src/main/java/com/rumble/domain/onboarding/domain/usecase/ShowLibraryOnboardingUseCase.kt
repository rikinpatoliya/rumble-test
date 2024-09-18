package com.rumble.domain.onboarding.domain.usecase

import android.content.Context
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.utils.RumbleConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val TAG = "ShowLibraryOnboardingUseCase"

class ShowLibraryOnboardingUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {

    operator fun invoke(versionCode: Int): Boolean {
        val packageManager = context.packageManager
        val packageName = context.packageName
        return try {
            val firstInstallTime = packageManager.getPackageInfo(packageName, 0).firstInstallTime
            val lastUpdateTime = packageManager.getPackageInfo(packageName, 0).lastUpdateTime
            (lastUpdateTime > firstInstallTime) && versionCode > RumbleConstants.VERSION_CODE_LIBRARY_FEATURE
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
            false
        }
    }
}