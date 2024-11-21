package com.rumble.domain.feed.domain.usecase

import androidx.paging.PagingData
import androidx.paging.insertSeparators
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdResult
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.domain.premium.domain.domainmodel.PremiumBanner
import com.rumble.domain.profile.domainmodel.AgeBracket
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepository
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import com.rumble.utils.RumbleConstants.AD_STEP
import com.rumble.utils.RumbleConstants.FIRST_AD_VIDEO_INDEX
import com.rumble.utils.extension.getUserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetFeedListUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    private val rumbleAdRepository: RumbleAdRepository,
    private val rumbleUnhandledErrorUseCase: UnhandledErrorUseCase,
    private val getKeywordsUseCase: CreateKeywordsUseCase,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
    private val sessionManager: SessionManager,
    private val userPreferenceManager: UserPreferenceManager,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    private val maxItemCount = 10
    private val cachedVideoList = mutableListOf<Feed>()

    operator fun invoke(videoCollectionType: VideoCollectionType, category: String): Flow<PagingData<Feed>> {
        val isPremium by lazy { runBlocking { sessionManager.isPremiumUserFlow.first() } }
        return feedRepository.fetchFeedList(videoCollectionType = videoCollectionType, pageSize = getVideoPageSizeUseCase())
            .map { pagingData ->
                pagingData.insertSeparators { _, after ->
                    if (after is VideoEntity) {
                        if (cachedVideoList.size >= maxItemCount) cachedVideoList.removeAt(0)
                        cachedVideoList.add(after)
                    }
                    null
                }
            }
            .map { pagingData ->
                var nonVideoCount = 0
                pagingData.insertSeparators { before, after ->
                    if (before != null && before !is VideoEntity && before !is RepostEntity) {
                        nonVideoCount++
                    }
                    after?.index?.let { index ->
                        if (isPremium.not() && ((index == FIRST_AD_VIDEO_INDEX + nonVideoCount) || (index > 0 && (index - FIRST_AD_VIDEO_INDEX - nonVideoCount) % AD_STEP == 0))) {
                            when (val nextAd = rumbleAdRepository.getNextAd(
                                keywords = getKeywordsUseCase(cachedVideoList),
                                categories = category,
                                currentUserId = sessionManager.userIdFlow.first().ifBlank { null }?.getUserId(),
                                gender = Gender.getByValue(sessionManager.userGenderFlow.first()),
                                ageBracket = AgeBracket.findBracketForAge(sessionManager.userAgeFlow.first())
                            )) {
                                is RumbleAdResult.RumbleAdSuccess -> {
                                    nextAd.rumbleAdEntity
                                }

                                is RumbleAdResult.RumbleAdError -> {
                                    rumbleErrorUseCase(nextAd.rumbleError)
                                    null
                                }

                                is RumbleAdResult.RumbleUncaughtError -> {
                                    rumbleUnhandledErrorUseCase(nextAd.tag, nextAd.exception)
                                    null
                                }
                            }
                        } else null
                    }
                }
            }
            .map { pagingData ->
                pagingData.insertSeparators { before, _ ->
                    if ((before is VideoEntity || before is RepostEntity) &&
                        before.index == 0 &&
                        isPremium.not() &&
                        runBlocking { userPreferenceManager.displayPremiumBannerFlow.first() }) {
                        PremiumBanner
                    } else null
                }
            }
    }
}