package com.rumble.domain.referrals.model

import com.rumble.domain.referrals.domain.domainmodel.ReferralDetailsEntity

interface ReferralsRepository {
    suspend fun getReferrals(): Result<ReferralDetailsEntity>
}