package com.rumble.domain.premium.domain.domainmodel

enum class SubscriptionType(val productId: String) {
    Annually("rumble-premium-yearly"),
    Monthly("rumble-premium-monthly");

    companion object {
        fun findByProductId(productId: String) = values().find { it.productId == productId }
    }
}