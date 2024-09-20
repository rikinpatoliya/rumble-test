package com.rumble.domain.landing.usecases

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.network.NetworkRumbleConstants.FIREBASE_CONFIG_FORCED_APP_VERSION
import javax.inject.Inject

private const val TAG = "ShouldForceNewAppVersionUseCase"

class ShouldForceNewAppVersionUseCase @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val isVersionNameGreaterUseCase: IsVersionNameGreaterUseCase,
) {

    operator fun invoke(versionName: String): Boolean {
        return try {
            val suggestedVersion =
                FirebaseRemoteConfig.getInstance().getString(FIREBASE_CONFIG_FORCED_APP_VERSION)
            isVersionNameGreaterUseCase(suggestedVersion, versionName)
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
            false
        }
    }
}