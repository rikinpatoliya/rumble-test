package com.rumble.battles.feedlist

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.feed.domain.domainmodel.video.UserVote
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.VoteVideoUseCase
import com.rumble.domain.feed.model.repository.FeedRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class VoteVideoUseCaseTests {

    private val repository = mockk<FeedRepository>(relaxed = true)
    private val videoEntity = mockk<VideoEntity>(relaxed = true)
    private val rumbleErrorUseCase = mockk<RumbleErrorUseCase>(relaxed = true)
    private val useCase = VoteVideoUseCase(repository, rumbleErrorUseCase)

    @Test
    fun testInvoke() = runBlocking {
        useCase.invoke(videoEntity, UserVote.LIKE)
        coVerify { repository.voteVideo(any(), any()) }
    }
}