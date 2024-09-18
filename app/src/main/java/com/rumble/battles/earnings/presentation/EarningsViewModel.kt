package com.rumble.battles.earnings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.earnings.domain.usecase.GetEarningsUseCase
import com.rumble.domain.earnings.domainmodel.EarningsEntity
import com.rumble.domain.earnings.domainmodel.EarningsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EarningsState(
    val earnings: EarningsEntity = EarningsEntity(),
    val loading: Boolean = false
)

sealed class EarningsVmEvent {
    object Error : EarningsVmEvent()
}

interface EarningsHandler {
    val state: StateFlow<EarningsState>
    val vmEvents: Flow<EarningsVmEvent>

    fun refresh()
}

private const val TAG = "EarningsViewModel"

@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    val getEarningsUseCase: GetEarningsUseCase
) : EarningsHandler, ViewModel() {

    override val state: MutableStateFlow<EarningsState> = MutableStateFlow(EarningsState())

    private val _vmEvents = Channel<EarningsVmEvent>(capacity = Channel.CONFLATED)
    override val vmEvents: Flow<EarningsVmEvent> = _vmEvents.receiveAsFlow()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        unhandledErrorUseCase(TAG, throwable)
    }

    init {
        loadData()
    }

    override fun refresh() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(errorHandler) {
            state.value = state.value.copy(loading = true)
            when (val result = getEarningsUseCase()) {
                is EarningsResult.Failure -> {
                    _vmEvents.trySend(EarningsVmEvent.Error)
                    state.value = state.value.copy(loading = false)
                }
                is EarningsResult.Success -> {
                    state.value = state.value.copy(loading = false, earnings = result.earnings)
                }
            }
        }

    }
}