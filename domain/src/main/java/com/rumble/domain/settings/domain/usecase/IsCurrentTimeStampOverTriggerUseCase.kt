package com.rumble.domain.settings.domain.usecase

import javax.inject.Inject

class IsCurrentTimeStampOverTriggerUseCase @Inject constructor() {

    operator fun invoke(
        lastTimeStamp: Long,
        triggerMillis: Long
    ): Boolean = (lastTimeStamp + triggerMillis) < System.currentTimeMillis()
}