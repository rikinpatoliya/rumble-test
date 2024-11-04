package com.rumble.domain.livechat.domain.domainmodel

enum class ChatMode {
    Free,
    PremiumOrSubscribedOnly;

    companion object {
        fun getByValue(value: Int) = when (value) {
            1 -> PremiumOrSubscribedOnly
            else -> Free
        }
    }
}