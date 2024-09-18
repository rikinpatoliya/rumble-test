package com.rumble.battles.settings.presentation

import androidx.lifecycle.SavedStateHandle
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.domain.usecases.GetUploadVideoUseCase
import com.rumble.domain.camera.domain.usecases.RestartUploadVideoUseCase
import com.rumble.domain.common.domain.usecase.IsDevelopModeUseCase
import com.rumble.domain.settings.domain.domainmodel.BackgroundPlay
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsEntity
import com.rumble.domain.settings.domain.usecase.GetAuthProvidersUseCase
import com.rumble.domain.settings.domain.usecase.GetCanSubmitLogsUseCase
import com.rumble.domain.settings.domain.usecase.GetNotificationSettingsUseCase
import com.rumble.domain.settings.domain.usecase.SendFeedbackUseCase
import com.rumble.domain.settings.domain.usecase.ShareLogsUseCase
import com.rumble.domain.settings.domain.usecase.UnlinkAuthProviderUseCase
import com.rumble.domain.settings.domain.usecase.UpdateNotificationSettingsUseCase
import com.rumble.domain.settings.domain.usecase.UpdateSubdomainUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.session.SessionManager
import com.rumble.network.subdomain.RumbleSubdomainUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class SettingsViewModelTest {

    private val userPreferenceManager = mockk<UserPreferenceManager>(relaxed = true)
    private val sessionManager = mockk<SessionManager>(relaxed = true)
    private val getNotificationSettingsUseCase =
        mockk<GetNotificationSettingsUseCase>(relaxed = true)
    private val updateNotificationSettingsUseCase =
        mockk<UpdateNotificationSettingsUseCase>(relaxed = true)
    private val entity =
        mockk<NotificationSettingsEntity>(relaxed = true)
    private val backgroundPlay =
        mockk<BackgroundPlay>(relaxed = true)
    private val getAuthProvidersUseCase: GetAuthProvidersUseCase = mockk(relaxed = true)
    private val unlinkAuthProviderUseCase: UnlinkAuthProviderUseCase = mockk(relaxed = true)
    private val rumbleSubdomainUseCase: RumbleSubdomainUseCase = mockk(relaxed = true)
    private val updateSubdomainUseCase: UpdateSubdomainUseCase = mockk(relaxed = true)
    private val unhandledErrorUseCase: UnhandledErrorUseCase = mockk(relaxed = true)
    private val getCanSubmitLogsUseCase: GetCanSubmitLogsUseCase = mockk(relaxed = true)
    private val getUploadVideoUseCase: GetUploadVideoUseCase = mockk(relaxed = true)
    private val restartUploadVideoUseCase: RestartUploadVideoUseCase = mockk(relaxed = true)
    private val shareLogsUseCase: ShareLogsUseCase = mockk(relaxed = true)
    private val isDevelopModeUseCase: IsDevelopModeUseCase = mockk(relaxed = true)
    private val sendFeedbackUseCase: SendFeedbackUseCase = mockk(relaxed = true)

    private lateinit var viewModel: SettingsViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = SettingsViewModel(
            savedState = SavedStateHandle(),
            getNotificationSettingsUseCase = getNotificationSettingsUseCase,
            sessionManager = sessionManager,
            rumbleSubdomainUseCase = rumbleSubdomainUseCase,
            updateSubdomainUseCase = updateSubdomainUseCase,
            updateNotificationSettingsUseCase = updateNotificationSettingsUseCase,
            getAuthProvidersUseCase = getAuthProvidersUseCase,
            unlinkAuthProviderUseCase = unlinkAuthProviderUseCase,
            userPreferenceManager = userPreferenceManager,
            unhandledErrorUseCase = unhandledErrorUseCase,
            getCanSubmitLogsUseCase = getCanSubmitLogsUseCase,
            getUploadVideoUseCase = getUploadVideoUseCase,
            restartUploadVideoUseCase = restartUploadVideoUseCase,
            shareLogsUseCase = shareLogsUseCase,
            isDevelopModeUseCase = isDevelopModeUseCase,
            sendFeedbackUseCase = sendFeedbackUseCase
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onToggleNotificationSettings() {
        viewModel.onToggleNotificationSettings(entity)
        coVerify { updateNotificationSettingsUseCase.invoke(entity) }
    }

    @Test
    fun onUpdateBackgroundPlay() {
        viewModel.onUpdateBackgroundPlay(backgroundPlay)
        coVerify { userPreferenceManager.saveBackgroundPlay(backgroundPlay) }
    }
}