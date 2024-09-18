package com.rumble.battles.videos.domain

import com.rumble.domain.video.domain.usecases.GenerateViewerIdUseCase
import com.rumble.network.session.SessionManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GenerateViewerIdUseCaseTests {

    private val slot = slot<String>()
    private val mockSessionManager: SessionManager = mockk(relaxed = true)
    private val generateViewerIdUseCase = GenerateViewerIdUseCase(mockSessionManager)

    @Before
    fun setup() {
        coEvery { mockSessionManager.saveViewerId(capture(slot)) } just Runs
        coEvery { mockSessionManager.viewerIdFlow } returns flowOf("")
    }

    @Test
    fun testGenerateViewerId() = runTest {
        repeat(100) {
            generateViewerIdUseCase()
            val value = slot.captured
            coVerify { mockSessionManager.saveViewerId(value) }
            assert(value.matches(Regex("^[a-zA-Z\\d_.-]{8}\$")))
        }
    }
}