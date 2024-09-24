package com.rumble.domain.landing.usecases

import com.onesignal.OneSignal
import javax.inject.Inject

class OneSignalExternalUserIdUseCase @Inject constructor() {
    operator fun invoke(userId: Int) = OneSignal.login(userId.toString())
}