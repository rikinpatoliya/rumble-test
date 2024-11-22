package com.rumble.battles.channels.channeldetails.domain.usecase

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsData
import com.rumble.domain.channels.channeldetails.domain.usecase.UpdateChannelNotificationsUseCase
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.sort.NotificationFrequency
import com.rumble.network.queryHelpers.Frequency
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class UpdateCreatorNotificationsUseCaseTest {
    private val repository = mockk<ChannelRepository>(relaxed = true)
    private val useCase = UpdateChannelNotificationsUseCase(repository)
    private val channelDetailsEntity = mockk<CreatorEntity>(relaxed = true)

    @Test
    operator fun invoke() = runBlocking {
        val action = UpdateChannelSubscriptionAction.SUBSCRIBE
        val data = UpdateChannelNotificationsData(
            pushNotificationsEnabled = true,
            emailNotificationsEnabled = true,
            notificationFrequency = NotificationFrequency(1, Frequency.INSTANT)
        )
        useCase.invoke(channelDetailsEntity, data)
        coVerify {
            repository.updateChannelSubscription(
                channelDetailsEntity.channelId,
                channelDetailsEntity.type,
                action,
                data
            )
        }
    }
}