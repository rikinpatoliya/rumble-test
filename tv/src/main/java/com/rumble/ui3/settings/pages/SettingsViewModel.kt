package com.rumble.ui3.settings.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.commonViews.dialogs.AlertDialogReason
import com.rumble.commonViews.dialogs.AlertDialogState
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.domainmodel.DebugAdType
import com.rumble.domain.settings.domain.usecase.GetCanUseAdsDebugMode
import com.rumble.domain.settings.domain.usecase.UpdateSubdomainUseCase
import com.rumble.domain.settings.model.UserPreferenceManager
import com.rumble.network.subdomain.RumbleSubdomain
import com.rumble.network.subdomain.RumbleSubdomainUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


interface SettingsHandler {
    val uiState: StateFlow<SettingsUiState>

    fun onDisableAdsChanged(disableAds: Boolean)
    fun onForceAdsChanged(forceAds: Boolean)
    fun onDisplayDebugAdChanged(displayDebugAd: Boolean)
    fun onSubdomainChanged(value: String)
    fun onUpdateSubdomain()
    fun onResetSubdomain()
    fun onDismissDialog()
}

sealed class SettingsAlertDialogReason : AlertDialogReason {
    data object ChangeSubdomainConfirmationDialog : SettingsAlertDialogReason()
}

data class SettingsUiState(
    val settingsVisible: Boolean = false,
    val disableAds: Boolean = false,
    val forceAds: Boolean = false,
    val displayDebugAd: Boolean = false,
    val canUseAdsDebugMode: Boolean = false,
    val subdomain: String,
    val rumbleSubdomain: RumbleSubdomain = RumbleSubdomain(),
    val alertDialogState: AlertDialogState = AlertDialogState(),
)

private const val TAG = "SettingsViewModel"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferenceManager: UserPreferenceManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val getCanUseAdsDebugMode: GetCanUseAdsDebugMode,
    private val rumbleSubdomainUseCase: RumbleSubdomainUseCase,
    private val updateSubdomainUseCase: UpdateSubdomainUseCase
) : ViewModel(), SettingsHandler {

    override val uiState = MutableStateFlow(SettingsUiState(subdomain = ""))

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    init {
        viewModelScope.launch(errorHandler) {
            val canUseAdsDebugMode = getCanUseAdsDebugMode()

            userPreferenceManager.disableAdsFlow.distinctUntilChanged().collectLatest {
                uiState.value = uiState.value.copy(
                    settingsVisible = canUseAdsDebugMode,
                    canUseAdsDebugMode = canUseAdsDebugMode,
                    disableAds = it
                )
            }
        }

        viewModelScope.launch {
            userPreferenceManager.forceAdsFlow.distinctUntilChanged().collectLatest {
                uiState.value = uiState.value.copy(
                    forceAds = it
                )
            }
        }

        viewModelScope.launch {
            userPreferenceManager.debugAdTypeFlow.distinctUntilChanged().collectLatest {
                uiState.value = uiState.value.copy(
                    displayDebugAd = it == DebugAdType.DEBUG_AD
                )
            }
        }

        viewModelScope.launch(errorHandler) {
            val rumbleSubdomainResult = rumbleSubdomainUseCase()
            uiState.update {
                it.copy(
                    subdomain = rumbleSubdomainResult.userInitiatedSubdomain
                        ?: rumbleSubdomainResult.appSubdomain
                        ?: rumbleSubdomainResult.environmentSubdomain,
                    rumbleSubdomain = rumbleSubdomainResult
                )
            }
        }
    }

    override fun onDisableAdsChanged(disableAds: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveDisableAdsMode(disableAds)
        }
    }

    override fun onForceAdsChanged(forceAds: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveForceAds(forceAds)
        }
    }

    override fun onDisplayDebugAdChanged(displayDebugAd: Boolean) {
        viewModelScope.launch {
            userPreferenceManager.saveDebugAdType(if (displayDebugAd) DebugAdType.DEBUG_AD else DebugAdType.REAL_AD)
        }
    }

    override fun onSubdomainChanged(value: String) {
        uiState.update { it.copy(subdomain = value.trim()) }
    }

    override fun onUpdateSubdomain() {
        viewModelScope.launch(errorHandler) {
            updateSubdomainUseCase(uiState.value.subdomain)
            val rumbleSubdomainResult = rumbleSubdomainUseCase()
            rumbleSubdomainResult.appSubdomain
            uiState.update {
                it.copy(
                    rumbleSubdomain = rumbleSubdomainResult,
                    alertDialogState = AlertDialogState(
                        show = true,
                        alertDialogReason = SettingsAlertDialogReason.ChangeSubdomainConfirmationDialog
                    )
                )
            }
        }
    }

    override fun onResetSubdomain() {
        viewModelScope.launch(Dispatchers.IO) {
            updateSubdomainUseCase(uiState.value.rumbleSubdomain.environmentSubdomain)
            val rumbleSubdomainResult = rumbleSubdomainUseCase()
            rumbleSubdomainResult.appSubdomain
            uiState.update {
                it.copy(
                    rumbleSubdomain = rumbleSubdomainResult,
                    subdomain = rumbleSubdomainResult.environmentSubdomain,
                    alertDialogState = AlertDialogState(
                        show = true,
                        alertDialogReason = SettingsAlertDialogReason.ChangeSubdomainConfirmationDialog
                    )
                )
            }
        }
    }

    override fun onDismissDialog() {
        uiState.update { it.copy(alertDialogState = AlertDialogState()) }
    }
}