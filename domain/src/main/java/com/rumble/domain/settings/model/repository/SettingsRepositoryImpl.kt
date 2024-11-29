package com.rumble.domain.settings.model.repository

import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.settings.domain.domainmodel.AuthProvidersResult
import com.rumble.domain.settings.domain.domainmodel.CanSubmitLogsResult
import com.rumble.domain.settings.domain.domainmodel.CloseAccountResult
import com.rumble.domain.settings.domain.domainmodel.ExpireUserSessionsResult
import com.rumble.domain.settings.domain.domainmodel.License
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsEntity
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsResult
import com.rumble.domain.settings.domain.domainmodel.UpdateUserDetailsResult
import com.rumble.domain.settings.model.datasource.SettingsLocalDataSource
import com.rumble.domain.settings.model.datasource.SettingsRemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val settingsRemoteDataSource: SettingsRemoteDataSource,
    private val settingsLocalDataSource: SettingsLocalDataSource,
    private val dispatcher: CoroutineDispatcher,
) : SettingsRepository {

    override suspend fun fetchNotificationSettings(): NotificationSettingsResult =
        withContext(dispatcher) {
            settingsRemoteDataSource.fetchNotificationSettings()
        }

    override suspend fun fetchAuthProviders(): AuthProvidersResult =
        withContext(dispatcher) {
            settingsRemoteDataSource.fetchAuthProviders()
        }

    override suspend fun fetchCanSubmitLogs(): CanSubmitLogsResult =
        withContext(dispatcher) {
            settingsRemoteDataSource.fetchCanSubmitLogs()
        }

    override suspend fun unlinkAuthProvider(loginType: LoginType): Boolean =
        withContext(dispatcher) {
            settingsRemoteDataSource.unlinkAuthProvider(loginType)
        }

    override suspend fun updateNotificationSettings(notificationSettingsEntity: NotificationSettingsEntity): NotificationSettingsResult =
        withContext(dispatcher) {
            settingsRemoteDataSource.updateNotificationSettings(notificationSettingsEntity)
        }

    override suspend fun updateEmail(email: String, password: String): UpdateUserDetailsResult =
        withContext(dispatcher) {
            settingsRemoteDataSource.updateEmail(email, password)
        }

    override suspend fun updatePassword(newPassword: String, currentPassword: String): UpdateUserDetailsResult =
        withContext(dispatcher) {
            settingsRemoteDataSource.updatePassword(newPassword, currentPassword)
        }

    override suspend fun closeAccount(): CloseAccountResult =
        withContext(dispatcher) {
            settingsRemoteDataSource.closeAccount()
        }

    override suspend fun fetchLicenseList(): List<License> = withContext(dispatcher) {
        settingsLocalDataSource.getLicences().map {
            License(
                componentName = it.moduleName ?: "",
                licenseName = it.moduleLicense ?: "",
                licenseUrl = it.moduleLicenseUrl ?: ""
            )
        }
    }

    override suspend fun expireUserSession(): ExpireUserSessionsResult = withContext(dispatcher) {
        settingsRemoteDataSource.expireUserSessions()
    }
}