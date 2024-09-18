package com.rumble.domain.license.domain.domainmodel

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Dependency(
    val moduleName: String? = null,
    val moduleUrl: String? = null,
    val moduleVersion: String? = null,
    val moduleLicense: String? = null,
    val moduleLicenseUrl: String? = null
)