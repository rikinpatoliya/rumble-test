package com.rumble.domain.premium.domain.domainmodel

import com.rumble.domain.R

enum class SubscriptionType(val productId: String, val titleId: Int) {
    Annually("rumble-premium-yearly", R.string.rumble_premium_yearly),
    Monthly("rumble-premium-monthly", R.string.rumble_premium_monthly);

    companion object {
        fun findByProductId(productId: String) = entries.find { it.productId == productId }
    }
}