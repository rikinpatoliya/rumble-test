package com.rumble.domain.referrals.domain.domainmodel

import java.math.BigDecimal

data class ReferralEntity(
    val id: String = "",
    val username: String = "",
    val thumb: String? = null,
    val commission: BigDecimal = BigDecimal(0),
    //This parameter may come from the server in the future, but for now it's a constant
    val currencySymbol: String = "$"
)
