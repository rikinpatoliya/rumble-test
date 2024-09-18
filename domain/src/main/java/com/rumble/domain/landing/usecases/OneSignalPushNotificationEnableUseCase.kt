package com.rumble.domain.landing.usecases

import com.onesignal.OneSignal
import com.rumble.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OneSignalPushNotificationEnableUseCase @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val rumbleNotificationOptOutCompletionHandler: RumbleNotificationOptOutCompletionHandler,
) {
    suspend operator fun invoke(enable: Boolean) = withContext(ioDispatcher) {
        OneSignal.disablePush(enable.not())
        if (enable.not()) rumbleNotificationOptOutCompletionHandler()
    }
}