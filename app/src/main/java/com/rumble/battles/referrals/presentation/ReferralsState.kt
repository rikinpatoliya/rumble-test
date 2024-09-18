package com.rumble.battles.referrals.presentation

import com.rumble.domain.referrals.domain.domainmodel.ReferralDetailsEntity

data class ReferralsState(
    val referralDetailsEntity: ReferralDetailsEntity? = null,
    val loading: Boolean = false,
    val referralUrl: String = ""
)
