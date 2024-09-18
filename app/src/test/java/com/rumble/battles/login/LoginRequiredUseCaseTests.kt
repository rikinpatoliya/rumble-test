package com.rumble.battles.login

import com.rumble.domain.landing.usecases.LoginRequiredUseCase
import com.rumble.network.session.SessionManager
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

class LoginRequiredUseCaseTests {

    private val sessionManager: SessionManager = mockk(relaxed = true)
    private val loginRequiredUseCase: LoginRequiredUseCase = LoginRequiredUseCase(sessionManager)


    @Test
    fun testFirstAppLaunch() = runBlocking {
        every { sessionManager.lastLoginPromptTimeFlow } returns MutableStateFlow(0)
        every { sessionManager.cookiesFlow } returns MutableStateFlow("")
        val result = loginRequiredUseCase()
        assert(result)
        coVerify { sessionManager.saveLastLoginPromptTime(any()) }
    }

    @Test
    fun testMoreThen30Days() = runBlocking {
        val lastDate = LocalDateTime.now().atOffset(ZoneOffset.UTC).minusDays(31).toEpochSecond()
        every { sessionManager.lastLoginPromptTimeFlow } returns MutableStateFlow(lastDate)
        every { sessionManager.cookiesFlow } returns MutableStateFlow("")
        val result = loginRequiredUseCase()
        assert(result)
        coVerify { sessionManager.saveLastLoginPromptTime(any()) }
    }

    @Test
    fun testLessThen30Days() = runBlocking {
        val lastDate = LocalDateTime.now().atOffset(ZoneOffset.UTC).minusDays(29).toEpochSecond()
        every { sessionManager.lastLoginPromptTimeFlow } returns MutableStateFlow(lastDate)
        every { sessionManager.cookiesFlow } returns MutableStateFlow("")
        val result = loginRequiredUseCase()
        assert(result.not())
    }

    @Test
    fun testMoreThen30DaysUserLoggedIn() = runBlocking {
        val lastDate = LocalDateTime.now().atOffset(ZoneOffset.UTC).minusDays(31).toEpochSecond()
        every { sessionManager.lastLoginPromptTimeFlow } returns MutableStateFlow(lastDate)
        every { sessionManager.cookiesFlow } returns MutableStateFlow("cookies")
        val result = loginRequiredUseCase()
        assert(result.not())
    }
}