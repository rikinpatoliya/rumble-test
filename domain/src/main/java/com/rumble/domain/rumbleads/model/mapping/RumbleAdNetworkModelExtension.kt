package com.rumble.domain.rumbleads.model.mapping

import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdEntity
import com.rumble.network.dto.ads.rumble.AdMetadata
import com.rumble.network.dto.ads.rumble.AdPlacement
import com.rumble.network.dto.ads.rumble.RumbleAd
import com.rumble.utils.extension.utcSecondTimestampToLocal
import com.rumble.videoplayer.domain.model.AdEntity
import com.rumble.videoplayer.domain.model.PreRollUrl
import com.rumble.videoplayer.domain.model.VideoAdDataEntity
import java.math.BigDecimal

fun RumbleAd.toAdEntity(): RumbleAdEntity =
    RumbleAdEntity(
        impressionUrl = impressionUrl,
        clickUrl = clickUrl,
        assetUrl = assetUrl,
        expirationLocal = expiration.utcSecondTimestampToLocal(),
        brand = native?.brand,
        title = native?.title,
        price = bidding?.let { BigDecimal(it.price) }
    )

fun AdPlacement.toAdEntity(): AdEntity =
    AdEntity(
        timeCode = TimeCodeMapper.parseTimeCode(timeCode),
        urlList = adDataList.map {
            PreRollUrl(
                url = it.url,
                requestedUrlList = it.events?.reqEvents ?: emptyList(),
                impressionUrlList = it.events?.impEvents ?: emptyList(),
                pgImpressionUrlList = it.events?.pgimpEvents ?: emptyList(),
                clickUrlList = it.events?.clkEvents ?: emptyList()
            )
        }.toMutableList(),
    )

fun AdMetadata.toAdDataEntity(): VideoAdDataEntity =
    VideoAdDataEntity(
        preRollList = adPlacementList.map { it.toAdEntity() },
        startUrlList = events?.startEvents ?: emptyList(),
        viewUrlList = events?.viewEvents ?: emptyList(),
        pgViewUrlList = events?.pgviewEvents ?: emptyList()
    )