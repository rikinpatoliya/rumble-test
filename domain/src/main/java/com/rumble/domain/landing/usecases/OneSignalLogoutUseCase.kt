package com.rumble.domain.landing.usecases

import com.onesignal.OneSignal
import javax.inject.Inject

class OneSignalLogoutUseCase @Inject constructor() {
    operator fun invoke() {
        OneSignal.User.pushSubscription.optOut()
        OneSignal.logout()
    }
}