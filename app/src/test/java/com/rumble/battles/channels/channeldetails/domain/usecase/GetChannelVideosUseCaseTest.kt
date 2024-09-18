package com.rumble.battles.channels.channeldetails.domain.usecase

import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelVideosUseCase
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.network.queryHelpers.Sort
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class GetChannelVideosUseCaseTest {

    private val repository = mockk<ChannelRepository>(relaxed = true)
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase = mockk(relaxed = true)
    private val useCase = GetChannelVideosUseCase(repository, getVideoPageSizeUseCase)

    @Test
    operator fun invoke() = runBlocking {
        val id = "12345"
        useCase.invoke(id)
        coVerify { repository.fetchChannelVideos(id, Sort.DATE, any()) }
    }
}