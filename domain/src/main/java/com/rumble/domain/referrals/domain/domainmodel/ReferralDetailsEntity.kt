package com.rumble.domain.referrals.domain.domainmodel

import java.math.BigDecimal

data class ReferralDetailsEntity(
    val referrals: MutableList<ReferralEntity> = mutableListOf<ReferralEntity>(),
    val impressionCount: Int = 0,
    val commissionTotal: BigDecimal = BigDecimal(0),
    val ticketTotal: Int = 0,
    val ownTicketCount: Int = 0,
    val referralTicketCount: Int = 0,
    //This parameter may come from the server in the future, but for now it's a constant
    val currencySymbol: String = "$"
)