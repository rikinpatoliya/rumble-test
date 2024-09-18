package com.rumble.domain.profile.domain

import com.rumble.domain.BuildConfig
import com.rumble.domain.profile.domainmodel.AppVersionEntity
import com.rumble.domain.profile.domainmodel.AppVersionVisibility
import com.rumble.network.di.AppVersion
import javax.inject.Inject

class GetAppVersionUseCase @Inject constructor(
    @AppVersion private val appVersion: String
) {
    operator fun invoke(): AppVersionEntity =
        AppVersionEntity(
            appVersion, if (BuildConfig.BUILD_TYPE == "release") {
                AppVersionVisibility.Hidden
            } else {
                AppVersionVisibility.Visible
            }
        )
}