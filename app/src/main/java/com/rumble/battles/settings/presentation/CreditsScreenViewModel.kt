package com.rumble.battles.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.domainmodel.License
import com.rumble.domain.settings.domain.usecase.CreditsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CreditsScreenViewModel"

interface CreditsScreenHandler {
    val uiState: StateFlow<LicenseScreenState>
    val vmEvents: Flow<CreditsScreenVmEvent>

    fun onLicenseClicked(license: License)
}

sealed class CreditsScreenVmEvent {
    data class Error(val errorMessage: String? = null) : CreditsScreenVmEvent()
    data class OpenWebView(val url: String) : CreditsScreenVmEvent()
}

data class LicenseScreenState(
    val licenseList: List<License> = emptyList(),
    val loading: Boolean = false,
)

@HiltViewModel
class CreditsScreenViewModel @Inject constructor(
    creditsUseCase: CreditsUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) : ViewModel(), CreditsScreenHandler {

    override val uiState = MutableStateFlow(LicenseScreenState())

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
        uiState.update { it.copy(loading = false) }
        emitVmEvent(CreditsScreenVmEvent.Error())
    }

    private val _vmEvents = Channel<CreditsScreenVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<CreditsScreenVmEvent> = _vmEvents.receiveAsFlow()

    init {
        uiState.update {
            it.copy(
                loading = true
            )
        }
        viewModelScope.launch(errorHandler) {
            uiState.update {
                it.copy(
                    licenseList = creditsUseCase(),
                    loading = false,
                )
            }
        }
    }

    override fun onLicenseClicked(license: License) {
        emitVmEvent(CreditsScreenVmEvent.OpenWebView(license.licenseUrl))
    }

    private fun emitVmEvent(event: CreditsScreenVmEvent) {
        _vmEvents.trySend(event)
    }
}