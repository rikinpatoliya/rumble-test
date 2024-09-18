package com.rumble.battles.videos.domain

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.video.domain.usecases.DefineCurrentWatchPositionUseCase
import com.rumble.domain.video.domain.usecases.GetLastPositionUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.concurrent.TimeUnit

class DefineCurrentWatchPositionUseCaseTests {

    private val videoId: Long = 12345
    private val videoEntity: VideoEntity = mockk(relaxed = true)
    private val getLastPositionUseCase: GetLastPositionUseCase = mockk(relaxed = true)

    @Test
    fun testApplyLastPositionFalse() = runTest {
        coEvery { getLastPositionUseCase(videoId) } returns 0
        every { videoEntity.lastPositionSeconds } returns null
        val defineCurrentWatchPositionUseCase = DefineCurrentWatchPositionUseCase(getLastPositionUseCase)
        val result = defineCurrentWatchPositionUseCase(videoEntity, false)
        assert(result == 0L)
    }

    @Test
    fun testApplyLastPositionFalseWithSavedLastPosition() = runTest {
        coEvery { getLastPositionUseCase(videoId) } returns 900
        every { videoEntity.lastPositionSeconds } returns null
        val defineCurrentWatchPositionUseCase = DefineCurrentWatchPositionUseCase(getLastPositionUseCase)
        val result = defineCurrentWatchPositionUseCase(videoEntity, false)
        assert(result == 0L)
    }

    @Test
    fun testApplyLastPositionFalseWithReceivedLastPosition() = runTest {
        coEvery { getLastPositionUseCase(videoId) } returns 0
        every { videoEntity.lastPositionSeconds } returns 900
        val defineCurrentWatchPositionUseCase = DefineCurrentWatchPositionUseCase(getLastPositionUseCase)
        val result = defineCurrentWatchPositionUseCase(videoEntity, false)
        assert(result == 0L)
    }

    @Test
    fun testSavedLastPositionLessThanOffset() = runTest {
        val lastSavePosition = TimeUnit.SECONDS.toMillis(900)
        every { videoEntity.duration } returns 1000
        every { videoEntity.id } returns videoId
        coEvery { getLastPositionUseCase(videoId) } returns lastSavePosition
        every { videoEntity.lastPositionSeconds } returns null
        val defineCurrentWatchPositionUseCase = DefineCurrentWatchPositionUseCase(getLastPositionUseCase)
        val result = defineCurrentWatchPositionUseCase(videoEntity, true)
        assert(result == lastSavePosition)
    }

    @Test
    fun testSavedLastPositionMoreThanOffset() = runTest {
        val lastSavePosition = TimeUnit.SECONDS.toMillis(996)
        every { videoEntity.duration } returns 1000
        every { videoEntity.id } returns videoId
        coEvery { getLastPositionUseCase(videoId) } returns lastSavePosition
        every { videoEntity.lastPositionSeconds } returns null
        val defineCurrentWatchPositionUseCase = DefineCurrentWatchPositionUseCase(getLastPositionUseCase)
        val result = defineCurrentWatchPositionUseCase(videoEntity, true)
        assert(result == 0L)
    }

    @Test
    fun testShortVideoWithSavedLastPosition() = runTest {
        val lastSavePosition = TimeUnit.SECONDS.toMillis(1)
        every { videoEntity.duration } returns 4
        every { videoEntity.id } returns videoId
        coEvery { getLastPositionUseCase(videoId) } returns lastSavePosition
        every { videoEntity.lastPositionSeconds } returns null
        val defineCurrentWatchPositionUseCase = DefineCurrentWatchPositionUseCase(getLastPositionUseCase)
        val result = defineCurrentWatchPositionUseCase(videoEntity, true)
        assert(result == 0L)
    }

    @Test
    fun testReceivedLastPositionLessThanOffset() = runTest {
        val lastSavePosition = TimeUnit.SECONDS.toMillis(900)
        val lastReceivedPositionSeconds = 800L
        every { videoEntity.duration } returns 1000
        every { videoEntity.id } returns videoId
        coEvery { getLastPositionUseCase(videoId) } returns lastSavePosition
        every { videoEntity.lastPositionSeconds } returns lastReceivedPositionSeconds
        val defineCurrentWatchPositionUseCase = DefineCurrentWatchPositionUseCase(getLastPositionUseCase)
        val result = defineCurrentWatchPositionUseCase(videoEntity, true)
        assert(result == TimeUnit.SECONDS.toMillis(lastReceivedPositionSeconds))
    }

    @Test
    fun testReceivedLastPositionMoreThanOffset() = runTest {
        val lastSavePosition = TimeUnit.SECONDS.toMillis(900)
        val lastReceivedPositionSeconds = 996L
        every { videoEntity.duration } returns 1000
        every { videoEntity.id } returns videoId
        coEvery { getLastPositionUseCase(videoId) } returns lastSavePosition
        every { videoEntity.lastPositionSeconds } returns lastReceivedPositionSeconds
        val defineCurrentWatchPositionUseCase = DefineCurrentWatchPositionUseCase(getLastPositionUseCase)
        val result = defineCurrentWatchPositionUseCase(videoEntity, true)
        assert(result == 0L)
    }
}