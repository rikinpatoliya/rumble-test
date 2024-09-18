package com.rumble.domain.landing.usecases

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import javax.inject.Inject

private const val TAG = "ShouldSuggestNewAppVersionUseCase"

class ShouldSuggestNewAppVersionUseCase @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val isVersionNameGreaterUseCase: IsVersionNameGreaterUseCase,
) {

    operator fun invoke(versionName: String): Boolean {
        return try {
            val suggestedVersion =
                FirebaseRemoteConfig.getInstance().getString("suggested_app_version")
            isVersionNameGreaterUseCase(suggestedVersion, versionName)
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
            false
        }
    }
}