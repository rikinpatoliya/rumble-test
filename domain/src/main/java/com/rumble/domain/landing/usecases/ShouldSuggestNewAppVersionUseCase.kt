package com.rumble.domain.landing.usecases

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.usecase.IsCurrentTimeStampOverTriggerUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.utils.RumbleConstants.SUGGESTED_APP_VERSION_DISPLAY_DELAY_MINUTES
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "ShouldSuggestNewAppVersionUseCase"

class ShouldSuggestNewAppVersionUseCase @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val isVersionNameGreaterUseCase: IsVersionNameGreaterUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val isCurrentTimeStampOverTriggerUseCase: IsCurrentTimeStampOverTriggerUseCase
) {

    operator fun invoke(versionName: String): Boolean {
        return try {
            val suggestedVersion =
                FirebaseRemoteConfig.getInstance().getString("suggested_app_version")
            isVersionNameGreaterUseCase(suggestedVersion, versionName)
                    && isCurrentTimeStampOverTriggerUseCase(
                lastTimeStamp = runBlocking { userPreferenceManager.lastNewVersionDisplayTimeStampFlow.first() },
                triggerMillis = TimeUnit.MINUTES.toMillis(
                    SUGGESTED_APP_VERSION_DISPLAY_DELAY_MINUTES
                )
            )
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
            false
        }
    }
}