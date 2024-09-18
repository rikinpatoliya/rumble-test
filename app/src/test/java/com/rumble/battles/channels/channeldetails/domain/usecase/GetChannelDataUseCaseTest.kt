package com.rumble.battles.channels.channeldetails.domain.usecase

import com.rumble.domain.channels.channeldetails.domain.usecase.GetChannelDataUseCase
import com.rumble.domain.channels.model.repository.ChannelRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class GetChannelDataUseCaseTest {

    private val repository = mockk<ChannelRepository>(relaxed = true)
    private val useCase = GetChannelDataUseCase(repository)

    @Test
    operator fun invoke() = runBlocking {
        val id = "12345"
        useCase.invoke(id)
        coVerify { repository.fetchChannelData(id) }
    }
}