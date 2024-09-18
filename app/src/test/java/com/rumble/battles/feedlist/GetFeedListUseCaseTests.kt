package com.rumble.battles.feedlist

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.feed.domain.domainmodel.collection.VideoCollectionType
import com.rumble.domain.feed.domain.usecase.CreateKeywordsUseCase
import com.rumble.domain.feed.domain.usecase.GetHomeListUseCase
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.domain.rumbleads.model.repository.RumbleAdRepository
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class GetFeedListUseCaseTests {

    private val repository = mockk<FeedRepository>(relaxed = true)
    private val rumbleAdRepository: RumbleAdRepository = mockk(relaxed = true)
    private val rumbleErrorUseCase: RumbleErrorUseCase = mockk(relaxed = true)
    private val unhandledErrorUseCase: UnhandledErrorUseCase = mockk(relaxed = true)
    private val getKeywordsUseCase: CreateKeywordsUseCase = mockk(relaxed = true)
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase = mockk(relaxed = true)
    private val isDevelopModeUseCase: IsDevelopModeUseCase = mockk(relaxed = true)
    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val userPreferenceManager: UserPreferenceManager = mockk(relaxed = true)
    private val useCase = GetHomeListUseCase(
        feedRepository = repository,
        rumbleAdRepository = rumbleAdRepository,
        rumbleUnhandledErrorUseCase = unhandledErrorUseCase,
        rumbleErrorUseCase = rumbleErrorUseCase,
        getKeywordsUseCase = getKeywordsUseCase,
        getVideoPageSizeUseCase = getVideoPageSizeUseCase,
        sessionManager = sessionManager,
        userPreferenceManager = userPreferenceManager
    )

    @Test
    fun testInvoke() {
        useCase.invoke(VideoCollectionType.MyFeed, "")
        verify { repository.fetchFeedList(VideoCollectionType.MyFeed, 20) }
    }
}