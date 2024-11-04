package com.rumble.domain.livechat.domain.domainmodel

import kotlin.random.Random

enum class RaidMessageType {
    Space,
    Pirate,
    Viking;

    companion object {
        fun getRandomType() = RaidMessageType.entries[Random.nextInt(entries.size)]
    }
}