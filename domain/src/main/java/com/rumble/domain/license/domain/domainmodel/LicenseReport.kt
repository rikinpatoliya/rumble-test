package com.rumble.domain.license.domain.domainmodel

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LicenseReport(
    val dependencies: ArrayList<Dependency>
)