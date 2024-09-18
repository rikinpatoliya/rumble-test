package com.rumble.domain.settings.model.datasource

import com.rumble.domain.license.domain.domainmodel.Dependency

interface SettingsLocalDataSource {

    suspend fun getLicences(): List<Dependency>
}