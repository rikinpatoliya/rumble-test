package com.rumble.domain.rumbleads.model.repository

import com.rumble.analytics.PRE_ROLL_FAILED
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdEntity
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdResult
import com.rumble.domain.profile.domainmodel.AgeBracket
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.rumbleads.domain.domainmodel.SendAdEventResult
import com.rumble.domain.rumbleads.domain.domainmodel.VideoAdListResult
import com.rumble.domain.rumbleads.model.datasource.RumbleAdsRemoteDataSource
import com.rumble.domain.rumbleads.model.mapping.toAdEntity
import com.rumble.domain.rumbleads.model.mapping.toAdDataEntity
import com.rumble.network.NetworkRumbleConstants.ADS_DEFAULT_COUNT
import com.rumble.network.queryHelpers.PublisherId
import com.rumble.utils.RumbleErrorConstants.ERROR_EMPTY_LIST
import com.rumble.utils.RumbleErrorConstants.ERROR_EMPTY_RESULT
import com.rumble.utils.RumbleErrorConstants.ERROR_EXPIRED_ADS
import com.rumble.utils.extension.getChannelId
import com.rumble.utils.extension.getUserId
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class RumbleAdRepositoryImpl(
    private val remoteDataSource: RumbleAdsRemoteDataSource,
    private val dispatcher: CoroutineDispatcher
) : RumbleAdRepository {

    companion object {
        private const val TAG = "RumbleAdRepository"
    }

    private var cachedAdList: MutableList<RumbleAdEntity> = mutableListOf()
    private var lastUseCategories: String? = null

    override suspend fun getNextAd(
        keywords: String,
        categories: String?,
        currentUserId: Long?,
        gender: Gender,
        ageBracket: AgeBracket?
    ): RumbleAdResult =
        withContext(dispatcher) {
            if (lastUseCategories != categories) {
                cachedAdList.clear()
                lastUseCategories = categories
            }
            var adResult: RumbleAdResult =
                RumbleAdResult.RumbleAdError(RumbleError(TAG, "", 0, ERROR_EMPTY_LIST))
            cachedAdList =
                cachedAdList.filter { it.expirationLocal.isAfter(LocalDateTime.now()) }
                    .toMutableList()
            if (cachedAdList.isEmpty()) {
                try {
                    val result = remoteDataSource.fetchRumbleAdList(
                        limit = ADS_DEFAULT_COUNT,
                        keywords = keywords,
                        categories = categories,
                        currentUserId = currentUserId,
                        genderId = gender.genderId,
                        ageBracketId = ageBracket?.bracketId
                    )
                    if (result.isSuccessful && result.body() != null) {
                        val adList = result.body()?.adList?.map { it.toAdEntity() }
                        cachedAdList = adList
                            ?.filter { it.expirationLocal.isAfter(LocalDateTime.now()) }
                            ?.toMutableList()
                            ?: mutableListOf()
                        if (adList.isNullOrEmpty()) {
                            adResult = RumbleAdResult.RumbleAdError(
                                RumbleError(
                                    tag = TAG,
                                    response = result.raw(),
                                    customMessage = ERROR_EMPTY_LIST
                                )
                            )
                        } else if (cachedAdList.isEmpty()) {
                            adResult = RumbleAdResult.RumbleAdError(
                                RumbleError(
                                    tag = TAG,
                                    response = result.raw(),
                                    customMessage = ERROR_EXPIRED_ADS
                                )
                            )
                        }
                    } else
                        adResult = RumbleAdResult.RumbleAdError(RumbleError(TAG, result.raw()))
                } catch (e: Exception) {
                    adResult = RumbleAdResult.RumbleUncaughtError(TAG, e)
                }
            }
            cachedAdList.firstOrNull()?.let {
                adResult = RumbleAdResult.RumbleAdSuccess(it)
                cachedAdList.removeFirstOrNull()
            }
            adResult
        }

    override suspend fun fetchSingleAd(
        keywords: String,
        videoId: Long?,
        channelId: String?,
        userId: String?,
        currentUserId: Long?,
        gender: Gender,
        ageBracket: AgeBracket?
    ): RumbleAdResult =
        withContext(dispatcher) {
            var adResult: RumbleAdResult
            try {
                val result = remoteDataSource.fetchRumbleAdList(
                    limit = 1,
                    keywords = keywords,
                    videoId = videoId,
                    channelId = channelId?.getChannelId(),
                    userId = userId?.getUserId(),
                    currentUserId = currentUserId,
                    genderId = gender.genderId,
                    ageBracketId = ageBracket?.bracketId
                )
                adResult = if (result.isSuccessful && result.body() != null) {
                    val ad = result.body()?.adList?.first()?.toAdEntity()
                    if (ad != null && ad.expirationLocal.isAfter(LocalDateTime.now()))
                        RumbleAdResult.RumbleAdSuccess(ad)
                    else {
                        RumbleAdResult.RumbleAdError(
                            RumbleError(
                                tag = TAG,
                                response = result.raw(),
                                customMessage = ERROR_EMPTY_RESULT
                            )
                        )
                    }
                } else {
                    RumbleAdResult.RumbleAdError(RumbleError(TAG, result.raw()))
                }
            } catch (e: Exception) {
                adResult = RumbleAdResult.RumbleUncaughtError(TAG, e)
            }
            adResult
        }

    override suspend fun reportAdImpression(impressionUrl: String) = withContext(dispatcher) {
        remoteDataSource.reportAdImpression(impressionUrl)
    }

    override suspend fun fetchVideoAdList(
        lastWatchedTime: Long,
        videoId: String,
        publisherId: PublisherId,
        ignoreParams: Boolean,
        adsDebug: Boolean,
        disableAdsFilter: Boolean,
        enableMidrolls: Int,
    ): VideoAdListResult = withContext(dispatcher) {
        try {
            val response = remoteDataSource.fetchAdList(
                lastWatchedTime = lastWatchedTime,
                videoId = videoId,
                publisherId = publisherId,
                ignoreParams = if (ignoreParams) 1 else null,
                adsDebug = if (adsDebug) 1 else null,
                enableMidrolls = enableMidrolls
            )
            val preRollMetadata = response.body()?.metadata
            if (response.isSuccessful) {
                if (preRollMetadata != null && preRollMetadata.adPlacementList.isNotEmpty()) {
                    val preRollList = preRollMetadata.adPlacementList
                        .filter { (it.autoplay != 0 && it.linear != 0) || disableAdsFilter }
                        .map {
                            it.copy(adDataList = it.adDataList.filter { ad -> (ad.autoplay != 0) || disableAdsFilter })
                        }
                    VideoAdListResult.Success(
                        preRollMetadata.copy(adPlacementList = preRollList).toAdDataEntity()
                    )
                } else {
                    VideoAdListResult.EmptyVideoAdList
                }
            } else {
                VideoAdListResult.Failure(RumbleError(PRE_ROLL_FAILED, response.raw()))
            }
        } catch (e: Exception) {
            VideoAdListResult.UncaughtError(PRE_ROLL_FAILED, e)
        }
    }

    override suspend fun sendAdEvent(urlList: List<String>, initTime: Long): SendAdEventResult {
        return try {
            urlList.forEach {
                remoteDataSource.sendAdEvent(it, initTime)
            }
            SendAdEventResult.Success
        } catch (e: Exception) {
            SendAdEventResult.UncaughtError(PRE_ROLL_FAILED, e)
        }
    }
}