package com.rumble.network.subdomain

import com.rumble.battles.network.BuildConfig
import com.rumble.network.session.SessionManager
import javax.inject.Inject

class SyncRumbleSubdomainUseCase @Inject constructor(
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(): String {

        val rumbleSubdomain = RumbleSubdomain(
            environmentSubdomain = BuildConfig.DEFAULT_SUBDOMAIN,
            appSubdomain = sessionManager.getAppSubdomain(),
            userInitiatedSubdomain = sessionManager.getUserInitiatedSubdomain(),
        )
        return if (rumbleSubdomain.userInitiatedSubdomain != null
            && rumbleSubdomain.userInitiatedSubdomain != rumbleSubdomain.appSubdomain
        ) {
            sessionManager.saveSubdomain(rumbleSubdomain.userInitiatedSubdomain)
            rumbleSubdomain.userInitiatedSubdomain
        } else rumbleSubdomain.appSubdomain ?: rumbleSubdomain.environmentSubdomain
    }
}