package com.rumble.domain.landing.usecases

import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.di.VersionCode
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SaveVersionCodeUseCase @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    @VersionCode private val versionCode: Int
) {
    suspend operator fun invoke() {
        val currentVersionCode = userPreferenceManager.currentVersionCodeFlow.first()
        if (currentVersionCode != versionCode) {
            userPreferenceManager.savePreviousVersionCode(if (currentVersionCode < 0) versionCode else currentVersionCode)
            userPreferenceManager.saveCurrentVersionCode(versionCode)
        }
    }
}