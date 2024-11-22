package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelType
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdEntity
import com.rumble.domain.feed.domain.domainmodel.ads.RumbleAdResult
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.profile.domainmodel.AgeBracket
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepository
import com.rumble.network.session.SessionManager
import com.rumble.utils.extension.getUserId
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSingleRumbleAddUseCase @Inject constructor(
    private val rumbleAdRepository: RumbleAdRepository,
    private val rumbleUnhandledErrorUseCase: UnhandledErrorUseCase,
    private val getKeywordsUseCase: CreateKeywordsUseCase,
    private val sessionManager: SessionManager,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(
        videoEntity: VideoEntity?,
        channelDetailsEntity: CreatorEntity?
    ): RumbleAdEntity? {
        return when (val rumbleAdResult =
            rumbleAdRepository.fetchSingleAd(
                keywords = getKeywordsUseCase(listOf(videoEntity)),
                videoId = videoEntity?.id,
                userId = if (channelDetailsEntity?.type == ChannelType.USER) channelDetailsEntity.channelId else null,
                channelId = if (channelDetailsEntity?.type == ChannelType.CHANNEL) channelDetailsEntity.channelId else null,
                currentUserId = sessionManager.userIdFlow.first().ifBlank { null }?.getUserId(),
                gender = Gender.getByValue(sessionManager.userGenderFlow.first()),
                ageBracket = AgeBracket.findBracketForAge(sessionManager.userAgeFlow.first())
            )) {
            is RumbleAdResult.RumbleAdSuccess -> {
                rumbleAdResult.rumbleAdEntity
            }

            is RumbleAdResult.RumbleAdError -> {
                rumbleErrorUseCase(rumbleAdResult.rumbleError)
                null
            }

            is RumbleAdResult.RumbleUncaughtError -> {
                rumbleUnhandledErrorUseCase(rumbleAdResult.tag, rumbleAdResult.exception)
                null
            }
        }
    }
}