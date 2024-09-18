package com.rumble.domain.rumbleads.model.repository

import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdResult
import com.rumble.domain.profile.domainmodel.AgeBracket
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.rumbleads.domain.domainmodel.SendAdEventResult
import com.rumble.domain.rumbleads.domain.domainmodel.VideoAdListResult
import com.rumble.network.queryHelpers.PublisherId

interface RumbleAdRepository {
    suspend fun getNextAd(
        keywords: String,
        categories: String?,
        currentUserId: Long?,
        gender: Gender,
        ageBracket: AgeBracket?
    ): RumbleAdResult

    suspend fun fetchSingleAd(
        keywords: String,
        videoId: Long?,
        channelId: String?,
        userId: String?,
        currentUserId: Long?,
        gender: Gender,
        ageBracket: AgeBracket?
    ): RumbleAdResult

    suspend fun reportAdImpression(impressionUrl: String)

    suspend fun fetchVideoAdList(
        lastWatchedTime: Long,
        videoId: String,
        publisherId: PublisherId,
        ignoreParams: Boolean,
        adsDebug: Boolean,
        disableAdsFilter: Boolean,
        enableMidrolls: Int,
    ): VideoAdListResult

    suspend fun sendAdEvent(urlList: List<String>, initTime: Long): SendAdEventResult
}