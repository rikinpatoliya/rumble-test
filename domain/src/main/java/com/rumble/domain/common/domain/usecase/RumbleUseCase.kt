package com.rumble.domain.common.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase

interface RumbleUseCase {
    val rumbleErrorUseCase: RumbleErrorUseCase
}