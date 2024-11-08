package com.rumble.domain.landing.usecases

import com.rumble.network.di.IoDispatcher
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirstAppLaunchSetPropertiesUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val setUserPropertiesUseCase: SetUserPropertiesUseCase,
    private val appsFlySetUserIdUseCase: AppsFlySetUserIdUseCase,
    private val setOneSignalUserTagsUseCase: SetOneSignalUserTagsUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(dispatcher)

    operator fun invoke() {
        scope.launch {
            val firstAppLaunch = sessionManager.firstAppLaunchFlow.first()
            if (firstAppLaunch) {
                setUserPropertiesUseCase("", false)
                setOneSignalUserTagsUseCase(false)
                appsFlySetUserIdUseCase("")
                sessionManager.saveFirstAppLaunch(false)
            }

        }
    }
}