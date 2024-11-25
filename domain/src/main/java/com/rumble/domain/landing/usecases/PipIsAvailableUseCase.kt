package com.rumble.domain.landing.usecases

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.rumble.domain.settings.domain.domainmodel.BackgroundPlay
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PipIsAvailableUseCase @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val userPreferenceManager: UserPreferenceManager,
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(): Boolean {
        val packageManager = applicationContext.packageManager
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) &&
            userPreferenceManager.backgroundPlayFlow.first() == BackgroundPlay.PICTURE_IN_PICTURE &&
            sessionManager.videDetailsOpenedFlow.first()
    }
}