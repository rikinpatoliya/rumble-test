package com.rumble.domain.earnings.domainmodel

import java.math.BigDecimal

data class EarningsEntity(
    val uploaded: Int = 0,
    val approved: Int = 0,
    val currentBalance: BigDecimal = BigDecimal(0),
    val cpm: BigDecimal = BigDecimal(0),
    val total: BigDecimal = BigDecimal(0),
    val rumble: BigDecimal = BigDecimal(0),
    val youtube: BigDecimal = BigDecimal(0),
    val partners: BigDecimal = BigDecimal(0),
    val approvedPercentage: Int = 0,
    //This parameter may come from the server in the future, but for now it's a constant
    val currencySymbol: String = "$"
)
