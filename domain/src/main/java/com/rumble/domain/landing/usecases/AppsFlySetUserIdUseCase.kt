package com.rumble.domain.landing.usecases

import com.appsflyer.AppsFlyerLib
import javax.inject.Inject

class AppsFlySetUserIdUseCase @Inject constructor() {
    operator fun invoke(userId: String) = AppsFlyerLib.getInstance().setCustomerUserId(userId)
}