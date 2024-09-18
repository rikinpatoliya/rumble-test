package com.rumble.domain.feed.domain.domainmodel.video

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class PpvEntity(
    val priceCents: Int,
    val isPurchased: Boolean,
    val purchaseDeadline: LocalDateTime? = null,
    val productId: String,
) : Parcelable