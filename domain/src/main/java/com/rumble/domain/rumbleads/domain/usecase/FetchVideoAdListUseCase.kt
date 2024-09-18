package com.rumble.domain.rumbleads.domain.usecase

import com.rumble.analytics.ImaFetchEmptyEvent
import com.rumble.analytics.ImaFetchEvent
import com.rumble.analytics.ImaFetchFailedEvent
import com.rumble.analytics.ImaFetchFilledEvent
import com.rumble.analytics.ImaVideoLoadedEvent
import com.rumble.analytics.ImaVideoNoAutoplayLoadedEvent
import com.rumble.analytics.PRE_ROLL_FAILED
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.rumbleads.domain.domainmodel.VideoAdListResult
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepository
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.queryHelpers.PublisherId
import com.rumble.videoplayer.domain.model.VideoAdDataEntity
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.math.ceil

class FetchVideoAdListUseCase @Inject constructor(
    private val rumbleAdRepository: RumbleAdRepository,
    private val sendAdEventUseCase: SendAdEventUseCase,
    private val analyticsEventUseCase: AnalyticsEventUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val createPreRollVideoIdUseCase: CreatePreRollVideoIdUseCase,
    private val userPreferenceManager: UserPreferenceManager,
    private val developModeUseCase: IsDevelopModeUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(
        videoId: Long,
        watchedTime: Float = 0f,
        initTime: Long,
        publisherId: PublisherId = PublisherId.AndroidApp,
        autoPlay: Boolean
    ): VideoAdDataEntity {
        return if (userPreferenceManager.disableAdsFlow.first()) {
            VideoAdDataEntity()
        } else {
            val watchedTimeSeconds = ceil(watchedTime.toDouble()).toLong()
            analyticsEventUseCase(ImaFetchEvent(watchedTimeSeconds), true)
            val result = rumbleAdRepository.fetchVideoAdList(
                lastWatchedTime = watchedTimeSeconds,
                videoId = createPreRollVideoIdUseCase(videoId),
                publisherId = publisherId,
                ignoreParams = userPreferenceManager.forceAdsFlow.first(),
                adsDebug = userPreferenceManager.playDebugAdFlow.first(),
                disableAdsFilter = developModeUseCase(),
                enableMidrolls = 1
            )
            when (result) {
                is VideoAdListResult.Failure -> {
                    analyticsEventUseCase(ImaFetchFailedEvent, true)
                    rumbleErrorUseCase(result.rumbleError)
                    VideoAdDataEntity()
                }

                is VideoAdListResult.Success -> {
                    sendAdEventUseCase(result.videoAdData.viewUrlList, initTime)
                    analyticsEventUseCase(ImaVideoLoadedEvent, true)
                    analyticsEventUseCase(ImaFetchFilledEvent, true)
                    if (autoPlay.not()) {
                        sendAdEventUseCase(
                            result.videoAdData.viewUrlList + result.videoAdData.pgViewUrlList,
                            initTime
                        )
                        analyticsEventUseCase(ImaVideoNoAutoplayLoadedEvent, true)
                    }
                    result.videoAdData
                }

                is VideoAdListResult.UncaughtError -> {
                    analyticsEventUseCase(ImaFetchFailedEvent, true)
                    unhandledErrorUseCase(PRE_ROLL_FAILED, result.exception)
                    VideoAdDataEntity()
                }

                is VideoAdListResult.EmptyVideoAdList -> {
                    analyticsEventUseCase(ImaFetchEmptyEvent, true)
                    VideoAdDataEntity()
                }
            }
        }
    }
}