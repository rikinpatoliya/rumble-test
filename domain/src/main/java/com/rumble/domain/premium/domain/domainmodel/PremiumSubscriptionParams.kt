package com.rumble.domain.premium.domain.domainmodel

import com.rumble.network.queryHelpers.SubscriptionSource

data class PremiumSubscriptionParams(
    val subscriptionData: PremiumSubscriptionData? = null,
    val videoId: Long? = null,
    val source: SubscriptionSource? = null
)