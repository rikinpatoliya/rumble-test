package com.rumble.domain.common.domain.usecase

import com.rumble.domain.BuildConfig
import com.rumble.network.Environment
import javax.inject.Inject

class IsDevelopModeUseCase @Inject constructor() {
    operator fun invoke() = BuildConfig.BUILD_TYPE == "qa"
        || BuildConfig.BUILD_TYPE == "debug"
        || com.rumble.battles.network.BuildConfig.ENVIRONMENT ==  Environment.QA
        || com.rumble.battles.network.BuildConfig.ENVIRONMENT ==  Environment.DEV
}