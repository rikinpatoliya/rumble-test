package com.rumble.domain.referrals.domain.usecase

import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class GetReferralLinkUseCase @Inject constructor(
    private val sessionManager: SessionManager
) {
    private val baseReferralUrl = "https://rumble.com/register/"

    suspend operator fun invoke(): Result<String> {
        val username = sessionManager.userNameFlow.firstOrNull()
        return if (username != null) {
            Result.success(baseReferralUrl + username)
        } else {
            Result.failure(RuntimeException("Error fetching referral URL"))
        }
    }
}