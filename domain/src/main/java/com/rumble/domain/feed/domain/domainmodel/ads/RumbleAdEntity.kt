package com.rumble.domain.feed.domain.domainmodel.ads

import com.rumble.domain.feed.domain.domainmodel.Feed
import java.math.BigDecimal
import java.time.LocalDateTime

data class RumbleAdEntity(
    val impressionUrl: String,
    val clickUrl: String,
    val assetUrl: String,
    val expirationLocal: LocalDateTime,
    val title: String? = null,
    val brand: String? = null,
    val price: BigDecimal? = null,
    var viewed: Boolean = false,
    override val index: Int = 0,
) : Feed
