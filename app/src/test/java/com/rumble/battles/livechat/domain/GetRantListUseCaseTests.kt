package com.rumble.battles.livechat.domain

import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.domain.livechat.domain.domainmodel.RantConfig
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import com.rumble.domain.livechat.domain.usecases.GetRantListUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class GetRantListUseCaseTests {

    private val rantDuration = 120
    private val rantPrice = BigDecimal("5")
    private val mockRantLevel: RantLevel = mockk(relaxed = true)
    private val mockMessage: LiveChatMessageEntity = mockk(relaxed = true)
    private val mockRantConfig: RantConfig = mockk(relaxed = true)

    private val getRantListUseCase = GetRantListUseCase()

    @Before
    fun setup() {
        every { mockMessage.rantPrice } returns rantPrice
        every { mockRantLevel.duration } returns rantDuration
        every { mockRantLevel.rantPrice } returns rantPrice
        every { mockRantConfig.levelList } returns listOf(mockRantLevel)
    }

    @Test
    fun testNewRantMessage() {
        every { mockMessage.timeReceived } returns LocalDateTime.now()
        val result = getRantListUseCase(listOf(mockMessage), mockRantConfig)
        assert(result.isNotEmpty())
        assert(result.first().messageEntity == mockMessage)
        assert(result.first().timeLeftPercentage == 1f)
    }

    @Test
    fun testExpiredRantMessage() {
        every { mockMessage.timeReceived } returns LocalDateTime.now().minusSeconds(rantDuration.toLong())
        val result = getRantListUseCase(listOf(mockMessage), mockRantConfig)
        assert(result.isEmpty())
    }

    @Test
    fun testPercentage() {
        every { mockMessage.timeReceived } returns LocalDateTime.now().minusSeconds(60L)
        val result = getRantListUseCase(listOf(mockMessage), mockRantConfig)
        assert(result.isNotEmpty())
        assert(result.first().messageEntity == mockMessage)
        assert(result.first().timeLeftPercentage == 0.5f)
    }
}