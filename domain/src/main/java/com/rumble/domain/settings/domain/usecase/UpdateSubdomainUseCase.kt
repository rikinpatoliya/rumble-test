package com.rumble.domain.settings.domain.usecase

import com.rumble.network.session.SessionManager
import javax.inject.Inject

class UpdateSubdomainUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {

    suspend operator fun invoke(subdomain: String) =
        sessionManager.saveUserInitiatedSubdomain(subdomain)
}