package com.rumble.battles.channels.channeldetails.domain.usecase

import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelSubscriptionUseCase
import com.rumble.domain.channels.model.repository.ChannelRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class UpdateChannelSubscriptionUseCaseTest {

    private val repository = mockk<ChannelRepository>(relaxed = true)
    private val analyticsEventUseCase = mockk<AnalyticsEventUseCase>(relaxed = true)
    private val useCase = UpdateChannelSubscriptionUseCase(repository, analyticsEventUseCase)
    private val channelDetailsEntity = mockk<ChannelDetailsEntity>(relaxed = true)

    @Test
    operator fun invoke() = runBlocking {
        val action = UpdateChannelSubscriptionAction.SUBSCRIBE
        useCase.invoke(channelDetailsEntity, action)
        coVerify {
            repository.updateChannelSubscription(
                channelDetailsEntity.channelId,
                channelDetailsEntity.type,
                action
            )
        }
    }
}