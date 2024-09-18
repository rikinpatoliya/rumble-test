package com.rumble.network.subdomain

import com.rumble.battles.network.BuildConfig
import com.rumble.network.session.SessionManager
import javax.inject.Inject

class RumbleSubdomainUseCase @Inject constructor(
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(): RumbleSubdomain {
        val rumbleSubdomain = RumbleSubdomain(
            environmentSubdomain = BuildConfig.DEFAULT_SUBDOMAIN,
            appSubdomain = sessionManager.getAppSubdomain(),
            userInitiatedSubdomain = sessionManager.getUserInitiatedSubdomain(),
        )
        return rumbleSubdomain.copy(
            canResetSubdomain = rumbleSubdomain.appSubdomain != null
                    && rumbleSubdomain.environmentSubdomain != rumbleSubdomain.appSubdomain
        )
    }
}