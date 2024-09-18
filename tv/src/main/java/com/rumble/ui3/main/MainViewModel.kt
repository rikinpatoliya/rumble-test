package com.rumble.ui3.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.common.domain.usecase.InternetConnectionUseCase
import com.rumble.domain.video.domain.usecases.GenerateViewerIdUseCase
import com.rumble.network.connection.InternetConnectionObserver
import com.rumble.network.connection.InternetConnectionState
import com.rumble.network.session.SessionManager
import com.rumble.util.LeftMenuView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
    private val generateViewerIdUseCase: GenerateViewerIdUseCase,
    private val internetConnectionObserver: InternetConnectionObserver,
    private val internetConnectionUseCase: InternetConnectionUseCase,
) : ViewModel() {
    val cookies = sessionManager.cookiesFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val userName = sessionManager.userNameFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val userAvatarUrl = sessionManager.userPictureFlow.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    private var connectionStateJob: Job = Job()

    val uiStateNew: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val connectionState: MutableLiveData<InternetConnectionState> = MutableLiveData<InternetConnectionState>()

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    init {
        viewModelScope.launch(errorHandler) {
            cookies.collectLatest { cookies ->
                if (cookies.isEmpty()) {
                    sessionManager.clearUserData()
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            generateViewerIdUseCase()
        }

        observeConnectionState()
    }

    fun goToLoginNew() {
        uiStateNew.value = LeftMenuView.LOGIN_MENU
    }

    fun goToBrowse() {
        uiStateNew.value = LeftMenuView.BROWSE_MENU
    }

    fun onError(e: Throwable) {
        unhandledErrorUseCase(TAG, e)
    }

    private fun observeConnectionState() {
        connectionStateJob = viewModelScope.launch(errorHandler) {
            connectionState.value = internetConnectionUseCase()
            internetConnectionObserver.connectivityFlow.collectLatest {
                connectionState.value = it
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            sessionManager.saveLivePingEndpoint(null)
        }
    }
}