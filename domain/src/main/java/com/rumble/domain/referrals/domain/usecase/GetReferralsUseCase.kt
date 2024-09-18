package com.rumble.domain.referrals.domain.usecase

import com.rumble.domain.referrals.model.ReferralsRepository
import javax.inject.Inject

class GetReferralsUseCase @Inject constructor(
    private val referralsRepository: ReferralsRepository
) {
    suspend operator fun invoke() = referralsRepository.getReferrals()
}