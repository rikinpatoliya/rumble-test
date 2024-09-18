package com.rumble.domain.common.domain.usecase

import com.rumble.domain.BuildConfig
import javax.inject.Inject

class IsDevelopModeUseCase @Inject constructor() {
    operator fun invoke() = BuildConfig.BUILD_TYPE == "qa" || BuildConfig.BUILD_TYPE == "debug"
}